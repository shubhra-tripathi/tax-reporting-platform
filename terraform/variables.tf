variable "aws_region" {
  description = "AWS region used by the tax reporting platform"
  type        = string
  default     = "eu-west-2"
}

variable "bucket_name" {
  description = "S3 bucket used by the tax reporting platform"
  type        = string
}

variable "state_machine_name" {
  description = "Tax reporting Step Functions state machine name"
  type        = string
  default     = "tax-reporting-workflow"
}