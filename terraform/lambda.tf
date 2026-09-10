resource "aws_lambda_function" "ingestion" {
  function_name = "tax-reporting-ingestion"

  role = aws_iam_role.ingestion_lambda_role.arn

  runtime = "java21"

  handler = "com.demo.taxreporting.ingestion.IngestionHandler::handleRequest"

  filename = "${path.module}/../ingestion-lambda/target/ingestion-lambda-1.0-SNAPSHOT.jar"

  source_code_hash = filebase64sha256(
    "${path.module}/../ingestion-lambda/target/ingestion-lambda-1.0-SNAPSHOT.jar"
  )

  memory_size = 512
  timeout     = 15

  environment {
    variables = {
      STATE_MACHINE_ARN = "arn:aws:states:${var.aws_region}:${data.aws_caller_identity.current.account_id}:stateMachine:${var.state_machine_name}"
    }
  }

  tags = {
    Project     = "tax-reporting-platform"
    Environment = "demo"
    ManagedBy   = "Terraform"
  }
}