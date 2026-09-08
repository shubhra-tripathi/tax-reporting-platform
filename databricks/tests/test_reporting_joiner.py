from decimal import Decimal
from datetime import datetime

from databricks.transformations.reporting_joiner import (
    join_postings_to_accounts,
    find_missing_account_references,
    join_to_tax_profiles,
    find_missing_tax_profiles,
    validate_unique_key,
    validate_join_cardinality
)


def test_postings_join_to_accounts_successfully(spark):

    postings_df = spark.createDataFrame(
        [
            (
                "P1",
                "A1",
                datetime(2026, 9, 8, 10, 0),
                "INTEREST",
                Decimal("100.00"),
                "GBP"
            ),
            (
                "P2",
                "A2",
                datetime(2026, 9, 8, 10, 5),
                "DIVIDEND",
                Decimal("200.00"),
                "GBP"
            )
        ],
        """
        postingId STRING,
        accountId STRING,
        postingTimestamp TIMESTAMP,
        incomeType STRING,
        amount DECIMAL(18,2),
        currency STRING
        """
    )

    accounts_df = spark.createDataFrame(
        [
            ("A1", "C1", Decimal("1000.00")),
            ("A2", "C2", Decimal("2000.00"))
        ],
        """
        accountId STRING,
        clientId STRING,
        balance DECIMAL(18,2)
        """
    )

    joined_df = join_postings_to_accounts(
        postings_df,
        accounts_df
    )

    assert joined_df.count() == 2
    assert find_missing_account_references(joined_df).count() == 0
    assert validate_join_cardinality(postings_df, joined_df)


def test_missing_account_reference_is_detected(spark):

    postings_df = spark.createDataFrame(
        [
            (
                "P1",
                "A1",
                datetime(2026, 9, 8, 10, 0),
                "INTEREST",
                Decimal("100.00"),
                "GBP"
            ),
            (
                "P2",
                "A2",
                datetime(2026, 9, 8, 10, 5),
                "DIVIDEND",
                Decimal("200.00"),
                "GBP"
            )
        ],
        """
        postingId STRING,
        accountId STRING,
        postingTimestamp TIMESTAMP,
        incomeType STRING,
        amount DECIMAL(18,2),
        currency STRING
        """
    )

    accounts_df = spark.createDataFrame(
        [
            ("A1", "C1", Decimal("1000.00"))
        ],
        """
        accountId STRING,
        clientId STRING,
        balance DECIMAL(18,2)
        """
    )

    joined_df = join_postings_to_accounts(
        postings_df,
        accounts_df
    )

    missing_df = find_missing_account_references(
        joined_df
    )

    assert joined_df.count() == 2
    assert missing_df.count() == 1


def test_account_reference_key_must_be_unique(spark):

    accounts_df = spark.createDataFrame(
        [
            ("A1", "C1"),
            ("A1", "C2")
        ],
        """
        accountId STRING,
        clientId STRING
        """
    )

    assert not validate_unique_key(
        accounts_df,
        "accountId"
    )


def test_tax_profile_join_successfully_enriches_client(spark):

    posting_account_df = spark.createDataFrame(
        [
            (
                "P1",
                "A1",
                "C1",
                datetime(2026, 9, 8, 10, 0),
                "INTEREST",
                Decimal("100.00"),
                "GBP",
                Decimal("1000.00")
            )
        ],
        """
        postingId STRING,
        accountId STRING,
        clientId STRING,
        postingTimestamp TIMESTAMP,
        incomeType STRING,
        amount DECIMAL(18,2),
        currency STRING,
        balance DECIMAL(18,2)
        """
    )

    tax_profiles_df = spark.createDataFrame(
        [
            (
                "C1",
                "GB",
                "NON_US",
                "REPORTABLE"
            )
        ],
        """
        clientId STRING,
        taxResidence STRING,
        fatcaStatus STRING,
        crsStatus STRING
        """
    )

    joined_df = join_to_tax_profiles(
        posting_account_df,
        tax_profiles_df
    )

    record = joined_df.first()

    assert joined_df.count() == 1
    assert record.taxResidence == "GB"
    assert record.fatcaStatus == "NON_US"
    assert record.crsStatus == "REPORTABLE"
    assert find_missing_tax_profiles(joined_df).count() == 0