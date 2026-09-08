from pyspark.sql import DataFrame
from pyspark.sql import functions as F


def join_postings_to_accounts(
        postings_df: DataFrame,
        accounts_df: DataFrame
) -> DataFrame:
    """
    Join compacted postings to account reference data using accountId.

    A left join is used so unmatched postings remain visible
    for reconciliation instead of disappearing silently.
    """

    return (
        postings_df.alias("p")
        .join(
            accounts_df.alias("a"),
            F.col("p.accountId") == F.col("a.accountId"),
            "left"
        )
        .select(
            F.col("p.postingId"),
            F.col("p.accountId"),
            F.col("a.clientId"),
            F.col("p.postingTimestamp"),
            F.col("p.incomeType"),
            F.col("p.amount"),
            F.col("p.currency"),
            F.col("a.balance")
        )
    )


def find_missing_account_references(
        posting_account_df: DataFrame
) -> DataFrame:
    """
    Return postings that could not be matched to account reference data.
    """

    return posting_account_df.filter(
        F.col("clientId").isNull()
    )


def join_to_tax_profiles(
        posting_account_df: DataFrame,
        tax_profiles_df: DataFrame
) -> DataFrame:
    """
    Join account-enriched postings to client tax profiles using clientId.

    A left join preserves records with missing tax reference data.
    """

    return (
        posting_account_df.alias("pa")
        .join(
            tax_profiles_df.alias("t"),
            F.col("pa.clientId") == F.col("t.clientId"),
            "left"
        )
        .select(
            F.col("pa.postingId"),
            F.col("pa.accountId"),
            F.col("pa.clientId"),
            F.col("pa.postingTimestamp"),
            F.col("pa.incomeType"),
            F.col("pa.amount"),
            F.col("pa.currency"),
            F.col("pa.balance"),
            F.col("t.taxResidence"),
            F.col("t.fatcaStatus"),
            F.col("t.crsStatus")
        )
    )


def find_missing_tax_profiles(
        joined_reporting_df: DataFrame
) -> DataFrame:
    """
    Return postings that could not be matched to client tax profile data.
    """

    return joined_reporting_df.filter(
        F.col("taxResidence").isNull()
    )


def validate_unique_key(
        df: DataFrame,
        key_column: str
) -> bool:
    """
    Return True when the supplied business key is unique.
    """

    total_count = df.count()

    unique_count = (
        df.select(key_column)
        .distinct()
        .count()
    )

    return total_count == unique_count


def validate_join_cardinality(
        input_df: DataFrame,
        joined_df: DataFrame
) -> bool:
    """
    Verify that a join has neither lost nor multiplied records.
    """

    return input_df.count() == joined_df.count()