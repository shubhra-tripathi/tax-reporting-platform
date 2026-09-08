import os
import sys

import pytest
from pyspark.sql import SparkSession


@pytest.fixture(scope="session")
def spark():

    python_executable = sys.executable

    os.environ["PYSPARK_PYTHON"] = python_executable
    os.environ["PYSPARK_DRIVER_PYTHON"] = python_executable

    spark_session = (
        SparkSession.builder
        .master("local[2]")
        .appName("tax-reporting-spark-tests")
        .config(
            "spark.pyspark.python",
            python_executable
        )
        .config(
            "spark.pyspark.driver.python",
            python_executable
        )
        .getOrCreate()
    )

    spark_session.sparkContext.setLogLevel("ERROR")

    yield spark_session

    spark_session.stop()