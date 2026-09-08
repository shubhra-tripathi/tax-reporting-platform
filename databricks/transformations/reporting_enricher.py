from pyspark.sql import DataFrame
from pyspark.sql import functions as F


def add_reporting_flags(df: DataFrame) -> DataFrame:
    """
    Add simplified FATCA and CRS reporting eligibility flags.

    These rules are synthetic and are used only for the
    technical demonstration.
    """

    return (
        df
        .withColumn(
            "fatcaReportable",
            F.when(
                F.col("fatcaStatus") == "US_PERSON",
                F.lit(True)
            ).otherwise(F.lit(False))
        )
        .withColumn(
            "crsReportable",
            F.when(
                F.col("crsStatus") == "REPORTABLE",
                F.lit(True)
            ).otherwise(F.lit(False))
        )
    )


def add_reporting_classification(df: DataFrame) -> DataFrame:
    """
    Classify each posting according to its FATCA/CRS
    reporting eligibility.
    """

    return df.withColumn(
        "reportingClassification",

        F.when(
            F.col("fatcaReportable")
            & F.col("crsReportable"),
            F.lit("FATCA_AND_CRS")
        )
        .when(
            F.col("fatcaReportable"),
            F.lit("FATCA_ONLY")
        )
        .when(
            F.col("crsReportable"),
            F.lit("CRS_ONLY")
        )
        .otherwise(
            F.lit("NON_REPORTABLE")
        )
    )


def enrich_reporting_data(df: DataFrame) -> DataFrame:
    """
    Execute the complete reporting enrichment transformation.
    """

    return (
        df
        .transform(add_reporting_flags)
        .transform(add_reporting_classification)
    )


def find_unclassified_records(df: DataFrame) -> DataFrame:
    """
    Return records without a reporting classification.
    """

    return df.filter(
        F.col("reportingClassification").isNull()
    )