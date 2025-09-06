#!/bin/bash

# Shipment Tracking API Startup Script

echo "🚀 Starting Shipment Tracking API..."
echo "📦 Building application..."

# Build the application
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo "🌐 Starting server on http://localhost:8080"
    echo "📖 API Documentation: http://localhost:8080/swagger-ui.html"
    echo "🩺 Health Check: http://localhost:8080/actuator/health"
    echo ""
    echo "Press Ctrl+C to stop the server"
    echo ""
    
    # Run the application
    java -jar target/shipment-tracking-api-1.0.0.jar
else
    echo "❌ Build failed!"
    exit 1
fi