output "s3_bucket_name" {
  value = aws_s3_bucket.tax_reporting.bucket
}

output "ingestion_lambda_name" {
  value = aws_lambda_function.ingestion.function_name
}

output "ingestion_lambda_arn" {
  value = aws_lambda_function.ingestion.arn
}