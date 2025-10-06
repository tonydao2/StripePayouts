# StripePayouts
A Spring Boot API Service that acts as a webhook for Siteflow to call when an order is placed, which thens tells Stripe to send payout to bank account with description of order everytime.

# Docker

# Build and run the Docker container
docker compose build app
docker compose up app -d

# Access the application logs
docker compose logs -f app

# Stop the application
docker compose down