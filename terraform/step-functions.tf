resource "aws_iam_role" "step_functions_role" {
  name = "StepFunctions-tax-reporting-workflow-role-1w90pltkf"

  path = "/service-role/"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Effect = "Allow"

        Principal = {
          Service = "states.amazonaws.com"
        }

        Action = "sts:AssumeRole"
      }
    ]
  })

  tags = {
    Project     = "tax-reporting-platform"
    Environment = "demo"
    ManagedBy   = "Terraform"
  }
}


resource "aws_sfn_state_machine" "tax_reporting" {
  name     = var.state_machine_name
  role_arn = aws_iam_role.step_functions_role.arn

  definition = file(
    "${path.module}/../step-functions/tax-reporting-workflow.asl.json"
  )

  type = "STANDARD"

  tags = {
    Project     = "tax-reporting-platform"
    Environment = "demo"
    ManagedBy   = "Terraform"
  }
}
