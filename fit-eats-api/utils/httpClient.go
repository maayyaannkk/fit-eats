package utils

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"net"
	"net/http"
	"net/url"
	"time"
)

// Define a reusable HTTP client with sensible defaults
var httpClient = &http.Client{
	Timeout: 10 * time.Second, // global safeguard
	Transport: &http.Transport{
		Proxy: http.ProxyFromEnvironment,
		DialContext: (&net.Dialer{
			Timeout:   5 * time.Second,
			KeepAlive: 30 * time.Second,
		}).DialContext,
		MaxIdleConns:        100,
		IdleConnTimeout:     90 * time.Second,
		TLSHandshakeTimeout: 5 * time.Second,
	},
}

// MakeGETRequest sends a GET request to a given URL with query params and returns parsed JSON
// Example usage:
// var result map[string]interface{}
// err := MakeGETRequest(ctx, "https://api.example.com/data", map[string]string{"id": "123"}, &result)
func MakeGETRequest(ctx context.Context, baseURL string, queryParams map[string]string, target any) error {
	if ctx == nil {
		return errors.New("context cannot be nil")
	}
	if baseURL == "" {
		return errors.New("baseURL cannot be empty")
	}

	// Validate URL
	parsedURL, err := url.Parse(baseURL)
	if err != nil {
		return fmt.Errorf("invalid base URL: %w", err)
	}

	// Add query parameters
	q := parsedURL.Query()
	for k, v := range queryParams {
		if k == "" || v == "" {
			continue // skip invalid params
		}
		q.Set(k, v)
	}
	parsedURL.RawQuery = q.Encode()

	// Create request with context
	req, err := http.NewRequestWithContext(ctx, http.MethodGet, parsedURL.String(), nil)
	if err != nil {
		return fmt.Errorf("failed to create request: %w", err)
	}

	// Add security headers (can be expanded)
	req.Header.Set("Accept", "application/json")
	req.Header.Set("User-Agent", "GoHttpClient/1.0")

	// Send request
	resp, err := httpClient.Do(req)
	if err != nil {
		// Check if it's a timeout or DNS/network issue
		if errors.Is(err, context.DeadlineExceeded) {
			return fmt.Errorf("request timed out: %w", err)
		}
		var netErr net.Error
		if errors.As(err, &netErr) && netErr.Timeout() {
			return fmt.Errorf("network timeout: %w", err)
		}
		return fmt.Errorf("network error: %w", err)
	}
	defer resp.Body.Close()

	// Validate response
	if resp.StatusCode < 200 || resp.StatusCode >= 300 {
		return fmt.Errorf("unexpected status code: %d", resp.StatusCode)
	}

	// Decode JSON safely
	decoder := json.NewDecoder(resp.Body)
	decoder.DisallowUnknownFields() // prevent silently ignoring unknown fields
	if err := decoder.Decode(target); err != nil {
		return fmt.Errorf("failed to parse JSON: %w", err)
	}

	return nil
}
