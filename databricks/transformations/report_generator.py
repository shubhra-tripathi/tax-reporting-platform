from pyspark.sql import DataFrame
from pyspark.sql import functions as F


def add_reporting_year(df: DataFrame) -> DataFrame:
    """
    Derive the reporting year from postingTimestamp.
    """

    return df.withColumn(
        "reportingYear",
        F.year(F.col("postingTimestamp"))
    )


def generate_fatca_report(df: DataFrame) -> DataFrame:
    """
    Select FATCA-reportable records for downstream reporting.
    """

    return (
        df
        .filter(F.col("fatcaReportable") == True)
        .select(
            "reportingYear",
            "postingId",
            "clientId",
            "accountId",
            "taxResidence",
            "postingTimestamp",
            "incomeType",
            "amount",
            "currency",
            "fatcaStatus"
        )
    )


def generate_crs_report(df: DataFrame) -> DataFrame:
    """
    Select CRS-reportable records for downstream reporting.
    """

    return (
        df
        .filter(F.col("crsReportable") == True)
        .select(
            "reportingYear",
            "postingId",
            "clientId",
            "accountId",
            "taxResidence",
            "postingTimestamp",
            "incomeType",
            "amount",
            "currency",
            "crsStatus"
        )
    )


def find_invalid_report_records(
        df: DataFrame
) -> DataFrame:
    """
    Return report records missing mandatory fields.
    """

    return df.filter(
        F.col("clientId").isNull()
        | F.col("accountId").isNull()
        | F.col("taxResidence").isNull()
        | F.col("amount").isNull()
        | F.col("currency").isNull()
        | F.col("reportingYear").isNull()
    )


def calculate_total_amount(
        df: DataFrame
):
    """
    Calculate the total monetary amount in a report population.
    """

    return (
        df
        .agg(
            F.sum("amount").alias("totalAmount")
        )
        .first()["totalAmount"]
    )