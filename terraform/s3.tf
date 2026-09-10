resource "aws_s3_bucket" "tax_reporting" {
  bucket = var.bucket_name

  tags = {
    Project     = "tax-reporting-platform"
    Environment = "demo"
    ManagedBy   = "Terraform"
  }
}