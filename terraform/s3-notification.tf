resource "aws_lambda_permission" "allow_s3" {
  statement_id  = "lambda-b1dc1cfa-6427-4ef7-81fc-b855e3c7dd57"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.ingestion.function_name
  principal     = "s3.amazonaws.com"

  source_arn     = aws_s3_bucket.tax_reporting.arn
  source_account = data.aws_caller_identity.current.account_id
}
resource "aws_s3_bucket_notification" "ingestion_trigger" {
  bucket = aws_s3_bucket.tax_reporting.id

  lambda_function {
    id                  = "45e1e8e2-2a0a-4dab-b054-4d3b0da41109"
    lambda_function_arn = aws_lambda_function.ingestion.arn
    events              = ["s3:ObjectCreated:*"]

    filter_prefix = "raw/"
    filter_suffix = ".csv"
  }

  depends_on = [
    aws_lambda_permission.allow_s3
  ]
}
