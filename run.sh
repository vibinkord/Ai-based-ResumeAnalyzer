#!/bin/bash
#
# Resume Analyzer Application Launcher
# Fast, simple, and reliable startup script
#
# This script runs the pre-built JAR file
# If JAR doesn't exist, it builds one first
#

set -e

# Get script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Print with color
print_header() {
    echo -e "${BLUE}╔════════════════════════════════════════════════════════╗${NC}"
    echo -e "${BLUE}║${NC}   AI Resume Analyzer - Application Launcher         ${BLUE}║${NC}"
    echo -e "${BLUE}╚════════════════════════════════════════════════════════╝${NC}"
    echo ""
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ️  $1${NC}"
}

# Main function
main() {
    print_header
    
    # Check if Java is installed
    print_info "Checking Java installation..."
    if ! command -v java &> /dev/null; then
        print_error "Java is not installed!"
        echo "Please install Java 17 or higher from https://adoptium.net/"
        exit 1
    fi
    
    JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "\K[^"]+' | head -1)
    print_success "Java $JAVA_VERSION found"
    echo ""
    
    # Check if JAR file exists
    JAR_FILE="target/resume-analyzer.jar"
    
    if [ ! -f "$JAR_FILE" ]; then
        print_info "JAR file not found. Building..."
        echo ""
        
        # Check if Maven is available
        if command -v mvn &> /dev/null; then
            print_info "Building with Maven..."
            mvn clean package -DskipTests
        elif [ -f "mvnw" ]; then
            print_info "Building with Maven wrapper..."
            chmod +x mvnw
            ./mvnw clean package -DskipTests
        else
            print_error "Maven not found and mvnw not available!"
            echo "Please install Maven or ensure mvnw exists"
            exit 1
        fi
        
        if [ ! -f "$JAR_FILE" ]; then
            print_error "Build failed! JAR file was not created."
            exit 1
        fi
        
        print_success "Build completed successfully!"
        echo ""
    fi
    
    # Verify JAR file size
    JAR_SIZE=$(du -h "$JAR_FILE" | cut -f1)
    print_success "JAR file ready ($JAR_SIZE)"
    echo ""
    
    # Kill any existing process on port 8082 (optional, graceful)
    if command -v lsof &> /dev/null; then
        if lsof -Pi :8082 -sTCP:LISTEN -t >/dev/null 2>&1; then
            print_info "Stopping existing process on port 8082..."
            lsof -Ti :8082 | xargs kill -9 2>/dev/null || true
            sleep 2
        fi
    fi
    
    # Start the application
    print_info "Starting application on port 8082..."
    echo ""
    echo -e "${GREEN}════════════════════════════════════════════════════════${NC}"
    echo -e "${GREEN}Application Starting...${NC}"
    echo -e "${GREEN}════════════════════════════════════════════════════════${NC}"
    echo ""
    
    java -jar "$JAR_FILE" --server.port=8082
    
    # If we get here, application stopped
    echo ""
    echo -e "${YELLOW}════════════════════════════════════════════════════════${NC}"
    echo -e "${YELLOW}Application stopped${NC}"
    echo -e "${YELLOW}════════════════════════════════════════════════════════${NC}"
}

# Run main function
main "$@"
