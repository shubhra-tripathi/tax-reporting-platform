from pyspark.sql import DataFrame
from pyspark.sql import functions as F


def normalize_posting_timestamps(df: DataFrame) -> DataFrame:
    """
    Convert raw ISO-8601 posting timestamps into Spark timestamps.

    Multiple timestamp patterns are supported so that a malformed
    timestamp does not fail the complete batch.
    """

    return (
        df.withColumn(
            "parsedTimestamp",
            F.coalesce(
                F.try_to_timestamp(
                    F.col("postingTimestamp"),
                    F.lit("yyyy-MM-dd'T'HH:mmXXX")
                ),
                F.try_to_timestamp(
                    F.col("postingTimestamp"),
                    F.lit("yyyy-MM-dd'T'HH:mm:ssXXX")
                ),
                F.try_to_timestamp(
                    F.col("postingTimestamp"),
                    F.lit("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                )
            )
        )
        .drop("postingTimestamp")
        .withColumnRenamed(
            "parsedTimestamp",
            "postingTimestamp"
        )
    )


def valid_posting_condition():
    """
    Define the data-quality rules that determine whether
    a posting is allowed into the compacted dataset.
    """

    return (
            F.col("accountId").isNotNull()
            & (F.trim(F.col("accountId")) != "")
            & F.col("postingTimestamp").isNotNull()
            & F.col("amount").isNotNull()
            & (F.col("amount") > 0)
    )


def get_valid_postings(df: DataFrame) -> DataFrame:
    """
    Return postings that satisfy all validation rules.
    """

    return df.filter(
        valid_posting_condition()
    )


def get_rejected_postings(df: DataFrame) -> DataFrame:
    """
    Return invalid postings together with a rejection reason.
    """

    return (
        df.filter(
            ~valid_posting_condition()
        )
        .withColumn(
            "rejectionReason",

            F.when(
                F.col("accountId").isNull()
                | (F.trim(F.col("accountId")) == ""),
                F.lit("ACCOUNT_ID_MISSING")
            )
            .when(
                F.col("postingTimestamp").isNull(),
                F.lit("INVALID_TIMESTAMP")
            )
            .when(
                F.col("amount").isNull(),
                F.lit("AMOUNT_MISSING")
            )
            .when(
                F.col("amount") <= 0,
                F.lit("AMOUNT_NOT_POSITIVE")
            )
            .otherwise(
                F.lit("UNKNOWN_VALIDATION_ERROR")
            )
        )
    )


def standardize_postings(df: DataFrame) -> DataFrame:
    """
    Normalize business fields so logically identical values
    are represented consistently.
    """

    return (
        df
        .withColumn(
            "postingId",
            F.trim(F.col("postingId"))
        )
        .withColumn(
            "accountId",
            F.trim(F.col("accountId"))
        )
        .withColumn(
            "incomeType",
            F.upper(
                F.trim(F.col("incomeType"))
            )
        )
        .withColumn(
            "currency",
            F.upper(
                F.trim(F.col("currency"))
            )
        )
    )


def deduplicate_postings(df: DataFrame) -> DataFrame:
    """
    Keep one posting for each unique postingId.
    """

    return df.dropDuplicates(
        ["postingId"]
    )


def compact_postings(df: DataFrame):
    """
    Execute the complete posting compaction pipeline.

    Returns:
        compacted_df
        rejected_df
    """

    normalized_df = (
        normalize_posting_timestamps(df)
    )

    valid_df = get_valid_postings(
        normalized_df
    )

    rejected_df = get_rejected_postings(
        normalized_df
    )

    compacted_df = (
        valid_df
        .transform(standardize_postings)
        .transform(deduplicate_postings)
    )

    return compacted_df, rejected_df