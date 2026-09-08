from datetime import datetime
from decimal import Decimal

from databricks.transformations.report_generator import (
    add_reporting_year,
    generate_fatca_report,
    generate_crs_report,
    find_invalid_report_records,
    calculate_total_amount
)


def create_report_test_data(spark):

    return spark.createDataFrame(
        [
            (
                "P1",
                "C1",
                "A1",
                "US",
                datetime(2026, 9, 8, 10, 0),
                "INTEREST",
                Decimal("100.00"),
                "GBP",
                "US_PERSON",
                "REPORTABLE",
                True,
                True
            ),
            (
                "P2",
                "C2",
                "A2",
                "GB",
                datetime(2026, 9, 8, 11, 0),
                "DIVIDEND",
                Decimal("200.00"),
                "GBP",
                "NON_US",
                "REPORTABLE",
                False,
                True
            )
        ],
        """
        postingId STRING,
        clientId STRING,
        accountId STRING,
        taxResidence STRING,
        postingTimestamp TIMESTAMP,
        incomeType STRING,
        amount DECIMAL(18,2),
        currency STRING,
        fatcaStatus STRING,
        crsStatus STRING,
        fatcaReportable BOOLEAN,
        crsReportable BOOLEAN
        """
    )

def test_reporting_year_is_derived(spark):

    input_df = create_report_test_data(spark)

    result_df = add_reporting_year(
        input_df
    )

    years = {
        row.reportingYear
        for row in result_df.collect()
    }

    assert years == {2026}

def test_fatca_report_contains_only_fatca_records(spark):

    input_df = add_reporting_year(
        create_report_test_data(spark)
    )

    fatca_df = generate_fatca_report(
        input_df
    )

    assert fatca_df.count() == 1

    record = fatca_df.first()

    assert record.postingId == "P1"
    assert record.fatcaStatus == "US_PERSON"

def test_crs_report_contains_all_crs_records(spark):

    input_df = add_reporting_year(
        create_report_test_data(spark)
    )

    crs_df = generate_crs_report(
        input_df
    )

    assert crs_df.count() == 2

def test_report_amount_reconciliation(spark):

    input_df = add_reporting_year(
        create_report_test_data(spark)
    )

    crs_df = generate_crs_report(
        input_df
    )

    total = calculate_total_amount(
        crs_df
    )

    assert total == Decimal("300.00")

def test_valid_report_has_no_missing_required_fields(spark):

    input_df = add_reporting_year(
        create_report_test_data(spark)
    )

    crs_df = generate_crs_report(
        input_df
    )

    invalid_df = find_invalid_report_records(
        crs_df
    )

    assert invalid_df.count() == 0

