#!/bin/bash
#
# Resume Analyzer Application Launcher
# This script builds and runs the Resume Analyzer application
#

echo "╔════════════════════════════════════════════════════════╗"
echo "║   AI RESUME ANALYZER - APPLICATION LAUNCHER            ║"
echo "╚════════════════════════════════════════════════════════╝"
echo ""

# Check Java installation
if ! command -v java &> /dev/null; then
    echo "❌ ERROR: Java is not installed!"
    echo "Please install Java 17 or higher"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | grep -oP '(?<=version ")(?:[0-9]+)' | head -1)
echo "✅ Java version: $JAVA_VERSION"

# Check Maven installation
if ! command -v mvn &> /dev/null; then
    echo "⚠️  WARNING: Maven not found. Using bundled mvn wrapper"
    MVN="./mvnw"
else
    MVN="mvn"
fi

# Get current directory
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JAR_FILE="$PROJECT_DIR/target/resume-analyzer.jar"

echo "📂 Project Directory: $PROJECT_DIR"
echo ""

# Option selection
echo "Choose an option:"
echo "1. Run pre-built JAR (fastest)"
echo "2. Build and Run (recommended)"
echo "3. Clean build and run"
echo "4. Run tests"
echo ""

read -p "Enter option (1-4): " OPTION

case $OPTION in
    1)
        echo ""
        echo "🚀 Running pre-built JAR..."
        if [ -f "$JAR_FILE" ]; then
            java -jar "$JAR_FILE" --server.port=8082
        else
            echo "❌ JAR file not found. Please build first: mvn clean package -DskipTests"
        fi
        ;;
    2)
        echo ""
        echo "🔨 Building application..."
        cd "$PROJECT_DIR"
        $MVN clean package -DskipTests
        
        if [ -f "$JAR_FILE" ]; then
            echo ""
            echo "✅ Build successful! Starting application..."
            java -jar "$JAR_FILE" --server.port=8082
        else
            echo "❌ Build failed!"
            exit 1
        fi
        ;;
    3)
        echo ""
        echo "🧹 Cleaning and rebuilding..."
        cd "$PROJECT_DIR"
        $MVN clean install -DskipTests
        
        if [ -f "$JAR_FILE" ]; then
            echo ""
            echo "✅ Build successful! Starting application..."
            java -jar "$JAR_FILE" --server.port=8082
        else
            echo "❌ Build failed!"
            exit 1
        fi
        ;;
    4)
        echo ""
        echo "🧪 Running tests..."
        cd "$PROJECT_DIR"
        $MVN clean test
        ;;
    *)
        echo "❌ Invalid option!"
        exit 1
        ;;
esac

echo ""
echo "╔════════════════════════════════════════════════════════╗"
echo "║  Application Started!                                   ║"
echo "║                                                         ║"
echo "║  🌐 Access URL: http://localhost:8082                   ║"
echo "║  📊 Health Check: http://localhost:8082/actuator/health ║"
echo "║  📝 Logs: tail -f logs/resume-analyzer.log              ║"
echo "║                                                         ║"
echo "║  Press Ctrl+C to stop the application                   ║"
echo "╚════════════════════════════════════════════════════════╝"
