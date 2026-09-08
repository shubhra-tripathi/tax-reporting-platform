from decimal import Decimal

from databricks.transformations.posting_compaction import compact_postings


def test_compaction(spark):

    data = [
        (
            " P001 ",
            " A001 ",
            "2026-09-08T10:00+01:00",
            " interest ",
            Decimal("100.00"),
            " gbp "
        ),
        (
            "P002",
            "A002",
            "2026-09-08T10:05+01:00",
            "DIVIDEND",
            Decimal("0.00"),
            "GBP"
        )
    ]

    schema = """
        postingId STRING,
        accountId STRING,
        postingTimestamp STRING,
        incomeType STRING,
        amount DECIMAL(18,2),
        currency STRING
    """

    input_df = spark.createDataFrame(
        data,
        schema
    )

    compacted_df, rejected_df = (
        compact_postings(input_df)
    )

    assert compacted_df.count() == 1
    assert rejected_df.count() == 1

    valid_record = compacted_df.first()

    assert valid_record.postingId == "P001"
    assert valid_record.accountId == "A001"
    assert valid_record.incomeType == "INTEREST"
    assert valid_record.currency == "GBP"

    rejected_record = rejected_df.first()

    assert (
            rejected_record.rejectionReason
            == "AMOUNT_NOT_POSITIVE"
    )

def test_duplicate_postings_are_removed(spark):

    data = [
        (
            "P100",
            "A100",
            "2026-09-08T10:00+01:00",
            "INTEREST",
            Decimal("100.00"),
            "GBP"
        ),
        (
            " P100 ",
            "A100",
            "2026-09-08T10:00+01:00",
            "INTEREST",
            Decimal("100.00"),
            "GBP"
        )
    ]

    schema = """
        postingId STRING,
        accountId STRING,
        postingTimestamp STRING,
        incomeType STRING,
        amount DECIMAL(18,2),
        currency STRING
    """

    input_df = spark.createDataFrame(
        data,
        schema
    )

    compacted_df, rejected_df = (
        compact_postings(input_df)
    )

    assert compacted_df.count() == 1
    assert rejected_df.count() == 0