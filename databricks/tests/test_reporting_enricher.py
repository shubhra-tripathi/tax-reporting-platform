from databricks.transformations.reporting_enricher import (
    enrich_reporting_data,
    find_unclassified_records
)


def create_tax_test_data(spark):

    return spark.createDataFrame(
        [
            (
                "P1",
                "C1",
                "US_PERSON",
                "REPORTABLE"
            ),
            (
                "P2",
                "C2",
                "US_PERSON",
                "NON_REPORTABLE"
            ),
            (
                "P3",
                "C3",
                "NON_US",
                "REPORTABLE"
            ),
            (
                "P4",
                "C4",
                "NON_US",
                "NON_REPORTABLE"
            )
        ],
        """
        postingId STRING,
        clientId STRING,
        fatcaStatus STRING,
        crsStatus STRING
        """
    )
def test_reporting_flags_are_applied(spark):

    input_df = create_tax_test_data(spark)

    enriched_df = enrich_reporting_data(
        input_df
    )

    results = {
        row.postingId: row
        for row in enriched_df.collect()
    }

    assert results["P1"].fatcaReportable is True
    assert results["P1"].crsReportable is True

    assert results["P2"].fatcaReportable is True
    assert results["P2"].crsReportable is False

    assert results["P3"].fatcaReportable is False
    assert results["P3"].crsReportable is True

    assert results["P4"].fatcaReportable is False
    assert results["P4"].crsReportable is False

def test_reporting_classification(spark):

    input_df = create_tax_test_data(spark)

    enriched_df = enrich_reporting_data(
        input_df
    )

    results = {
        row.postingId:
            row.reportingClassification
        for row in enriched_df.collect()
    }

    assert results["P1"] == "FATCA_AND_CRS"
    assert results["P2"] == "FATCA_ONLY"
    assert results["P3"] == "CRS_ONLY"
    assert results["P4"] == "NON_REPORTABLE"

def test_enrichment_preserves_record_count(spark):

    input_df = create_tax_test_data(spark)

    enriched_df = enrich_reporting_data(
        input_df
    )

    assert enriched_df.count() == input_df.count()

def test_every_record_is_classified(spark):

    input_df = create_tax_test_data(spark)

    enriched_df = enrich_reporting_data(
        input_df
    )

    unclassified_df = (
        find_unclassified_records(
            enriched_df
        )
    )

    assert unclassified_df.count() == 0