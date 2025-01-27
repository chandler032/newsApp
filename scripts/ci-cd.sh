#!/bin/bash
set -e # Exit on error

echo "Starting CI/CD pipeline..."

# Build the application
./scripts/build.sh

# Run tests
./scripts/test.sh

# Deploy the application
./scripts/deploy.sh

echo "CI/CD pipeline completed successfully!"