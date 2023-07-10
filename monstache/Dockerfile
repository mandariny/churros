# Builder stage
FROM golang:1.17-alpine AS builder

RUN apk add --no-cache git gcc musl-dev

# Set the working directory
WORKDIR /app

# Download and install monstache (use specific version instead of latest)
RUN go get -u -d github.com/rwynn/monstache/v6

# Build monstache
RUN go install github.com/rwynn/monstache/v6@latest

# Final stage
FROM alpine:latest

# Install ca-certificates
RUN apk --no-cache add ca-certificates

# Copy binary from builder stage
COPY --from=builder /go/bin/monstache /usr/local/bin/

# COPY ./config /etc/monstache/config
CMD ["monstache", "-f", "/etc/monstache/config/config.toml"]
