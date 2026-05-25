# BetAPI - Google Sheets Integration Guide

## Overview

BetAPI is a Spring Boot REST API that reads sports match data from Google Sheets and exposes it through secured endpoints. The application uses Google OAuth2 authentication to ensure only authorized users can access the data.

## Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Google Cloud Console Setup](#google-cloud-console-setup)
3. [Application Components](#application-components)
4. [Configuration](#configuration)
5. [API Endpoints](#api-endpoints)
6. [Frontend Integration](#frontend-integration)
7. [Running the Application](#running-the-application)
8. [Testing](#testing)

---

## Architecture Overview

### Technology Stack
- **Spring Boot 4.0.0** - Backend framework
- **Java 21** - Programming language
- **Google Sheets API v4** - Data source
- **OAuth2 Resource Server** - Authentication
- **Lombok** - Reduce boilerplate code
- **Maven** - Dependency management

### Data Model

The application handles **Match** entities with the following fields:
- `matchNumber` (Integer) - Unique match identifier
- `roundNumber` (Integer) - Round/week number
- `dateTime` (LocalDateTime) - When the match takes place
- `location` (String) - Venue/stadium
- `homeTeam` (String) - Home team name
- `awayTeam` (String) - Away team name
- `result` (String) - Match result (e.g., "2-1" or "TBD")

---

## Google Cloud Console Setup

### Step 1: Create a Google Cloud Project

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Click on **Select a project** → **NEW PROJECT**
3. Enter project name: `BetAPI` (or your preferred name)
4. Click **CREATE**

### Step 2: Enable Google Sheets API

1. In the Google Cloud Console, go to **APIs & Services** → **Library**
2. Search for **Google Sheets API**
3. Click on it and press **ENABLE**

### Step 3: Create Service Account (for Server-to-Server Communication)

1. Go to **APIs & Services** → **Credentials**
2. Click **CREATE CREDENTIALS** → **Service Account**
3. Fill in the details:
   - **Service account name**: `betapi-sheets-reader`
   - **Service account ID**: (auto-generated)
   - **Description**: "Service account for reading Google Sheets data"
4. Click **CREATE AND CONTINUE**
5. Grant role: **Project** → **Viewer** (or no role needed for basic access)
6. Click **CONTINUE** → **DONE**

### Step 4: Create Service Account Key

1. Click on the newly created service account
2. Go to **KEYS** tab
3. Click **ADD KEY** → **Create new key**
4. Select **JSON** format
5. Click **CREATE**
6. The JSON key file will be downloaded automatically
7. Rename it to `credentials.json` and move it to:
   ```
   /src/main/resources/credentials.json
   ```

### Step 5: Configure OAuth2 for Frontend Authentication

1. Go to **APIs & Services** → **Credentials**
2. Click **CREATE CREDENTIALS** → **OAuth client ID**
3. If prompted, configure the OAuth consent screen:
   - **User Type**: External
   - **App name**: BetAPI
   - **User support email**: Your email
   - **Developer contact**: Your email
   - **Scopes**: Add `openid`, `email`, `profile`
   - Click **SAVE AND CONTINUE**

4. Create OAuth Client ID:
   - **Application type**: Web application
   - **Name**: BetAPI Frontend
   - **Authorized JavaScript origins**: 
     - `http://localhost:3000` (for local development)
     - `https://yourdomain.com` (for production)
   - **Authorized redirect URIs**:
     - `http://localhost:3000/callback`
     - `https://yourdomain.com/callback`
   - Click **CREATE**

5. Save your **Client ID** - you'll need this in your frontend

### Step 6: Share Google Spreadsheet with Service Account

1. Open your Google Spreadsheet
2. Click **Share** button
3. Add the service account email (found in `credentials.json` as `client_email`)
   - Example: `betapi-sheets-reader@betapi-123456.iam.gserviceaccount.com`
4. Grant **Viewer** permission
5. Click **Send** (uncheck "Notify people")

---

## Application Components

### 1. Model Layer (`Match.java`)

**Purpose**: Represents a single match entity

**Key Features**:
- Uses Lombok annotations to reduce boilerplate (`@Data`, `@Builder`)
- All fields are nullable to handle incomplete data
- LocalDateTime for date handling

```java
@Data
@Builder
public class Match {
    private Integer matchNumber;
    private Integer roundNumber;
    private LocalDateTime dateTime;
    private String location;
    private String homeTeam;
    private String awayTeam;
    private String result;
}
```

### 2. Configuration Layer

#### `GoogleSheetsConfig.java`

**Purpose**: Configures Google Sheets API client

**How it works**:
1. Reads service account credentials from `credentials.json`
2. Creates a scoped credential with read-only access to spreadsheets
3. Builds and provides a `Sheets` service bean

**Security**: Uses service account authentication for server-side access

#### `SecurityConfig.java`

**Purpose**: Configures OAuth2 security for REST endpoints

**How it works**:
1. Configures JWT token validation
2. Sets Google as the token issuer
3. Protects all `/api/*` endpoints (except `/api/public/*`)
4. Validates bearer tokens from frontend

**Key Points**:
- Requires valid JWT from Google OAuth
- Validates token signature and issuer
- Extracts user information from JWT

### 3. Service Layer (`GoogleSheetsService.java`)

**Purpose**: Handles communication with Google Sheets

**Key Features**:
- Reads data from specified spreadsheet and range
- Parses rows into Match objects
- Handles multiple date formats
- Error handling and logging
- Skips header row

**Methods**:
- `getMatches()`: Retrieves all matches from the sheet
- `parseRowToMatch()`: Converts a row into a Match object
- `parseInteger()`, `parseString()`, `parseDateTime()`: Type-safe parsing

**Supported Date Formats**:
- `yyyy-MM-dd HH:mm:ss`
- `yyyy-MM-dd HH:mm`
- `dd/MM/yyyy HH:mm:ss`
- `dd/MM/yyyy HH:mm`
- ISO LocalDateTime format

### 4. Controller Layer (`MatchController.java`)

**Purpose**: Exposes REST endpoints for frontend consumption

**Endpoints**:

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/matches` | Get all matches | Yes |
| GET | `/api/matches/{matchNumber}` | Get specific match | Yes |
| GET | `/api/matches/round/{roundNumber}` | Get matches by round | Yes |
| GET | `/api/health` | Health check | Yes |

**Features**:
- CORS enabled for frontend integration
- JWT authentication on all endpoints
- Logs user information from JWT
- Error handling with appropriate HTTP status codes

---

## Configuration

### `application.properties`

```properties
# Application
spring.application.name=betapi
server.port=8080

# Google Sheets Configuration
google.spreadsheet.id=YOUR_SPREADSHEET_ID_HERE
google.spreadsheet.range=Sheet1!A1:G100
google.credentials.file.path=src/main/resources/credentials.json

# OAuth2 Resource Server
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://accounts.google.com
```

### Configuration Steps:

1. **Get your Spreadsheet ID**:
   - Open your Google Spreadsheet
   - Look at the URL: `https://docs.google.com/spreadsheets/d/{SPREADSHEET_ID}/edit`
   - Copy the ID and replace `YOUR_SPREADSHEET_ID_HERE`

2. **Set the correct range**:
   - Format: `SheetName!StartCell:EndCell`
   - Example: `Sheet1!A1:G100` reads columns A-G, rows 1-100
   - Adjust based on your data size

3. **Expected Spreadsheet Format**:

   | Match# | Round# | DateTime | Location | Home Team | Away Team | Result |
   |--------|--------|----------|----------|-----------|-----------|--------|
   | 1 | 1 | 2025-01-15 18:00 | Stadium A | Team A | Team B | 2-1 |
   | 2 | 1 | 2025-01-15 20:00 | Stadium B | Team C | Team D | TBD |

---

## API Endpoints

### Authentication

All endpoints require a valid Google OAuth2 Bearer token in the Authorization header:

```http
Authorization: Bearer {GOOGLE_JWT_TOKEN}
```

### 1. Get All Matches

**Request**:
```http
GET /api/matches
Authorization: Bearer {token}
```

**Response** (200 OK):
```json
[
  {
    "matchNumber": 1,
    "roundNumber": 1,
    "dateTime": "2025-01-15T18:00:00",
    "location": "Stadium A",
    "homeTeam": "Team A",
    "awayTeam": "Team B",
    "result": "2-1"
  },
  {
    "matchNumber": 2,
    "roundNumber": 1,
    "dateTime": "2025-01-15T20:00:00",
    "location": "Stadium B",
    "homeTeam": "Team C",
    "awayTeam": "Team D",
    "result": "TBD"
  }
]
```

### 2. Get Match by Number

**Request**:
```http
GET /api/matches/1
Authorization: Bearer {token}
```

**Response** (200 OK):
```json
{
  "matchNumber": 1,
  "roundNumber": 1,
  "dateTime": "2025-01-15T18:00:00",
  "location": "Stadium A",
  "homeTeam": "Team A",
  "awayTeam": "Team B",
  "result": "2-1"
}
```

**Response** (404 Not Found) - if match doesn't exist

### 3. Get Matches by Round

**Request**:
```http
GET /api/matches/round/1
Authorization: Bearer {token}
```

**Response** (200 OK):
```json
[
  {
    "matchNumber": 1,
    "roundNumber": 1,
    "dateTime": "2025-01-15T18:00:00",
    "location": "Stadium A",
    "homeTeam": "Team A",
    "awayTeam": "Team B",
    "result": "2-1"
  },
  {
    "matchNumber": 2,
    "roundNumber": 1,
    "dateTime": "2025-01-15T20:00:00",
    "location": "Stadium B",
    "homeTeam": "Team C",
    "awayTeam": "Team D",
    "result": "TBD"
  }
]
```

### 4. Health Check

**Request**:
```http
GET /api/health
```

**Response** (200 OK):
```
BetAPI is running
```

---

## Frontend Integration

### Google OAuth2 Login Flow

#### 1. Install Google OAuth Library (React Example)

```bash
npm install @react-oauth/google
```

#### 2. Wrap App with Google OAuth Provider

```jsx
import { GoogleOAuthProvider } from '@react-oauth/google';

function App() {
  return (
    <GoogleOAuthProvider clientId="YOUR_GOOGLE_CLIENT_ID">
      <YourApp />
    </GoogleOAuthProvider>
  );
}
```

#### 3. Implement Login Component

```jsx
import { useGoogleLogin } from '@react-oauth/google';

function LoginButton() {
  const login = useGoogleLogin({
    onSuccess: (tokenResponse) => {
      // Store the access token
      localStorage.setItem('google_token', tokenResponse.access_token);
      
      // Now you can call your API
      fetchMatches(tokenResponse.access_token);
    },
    onError: () => {
      console.log('Login Failed');
    },
  });

  return <button onClick={login}>Sign in with Google</button>;
}
```

#### 4. Call BetAPI with Token

```javascript
async function fetchMatches(accessToken) {
  try {
    const response = await fetch('http://localhost:8080/api/matches', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json',
      },
    });

    if (response.ok) {
      const matches = await response.json();
      console.log('Matches:', matches);
      return matches;
    } else {
      console.error('Failed to fetch matches:', response.status);
    }
  } catch (error) {
    console.error('Error:', error);
  }
}
```

#### 5. Complete Example with Error Handling

```javascript
import React, { useState, useEffect } from 'react';
import { GoogleOAuthProvider, useGoogleLogin } from '@react-oauth/google';

const GOOGLE_CLIENT_ID = 'YOUR_GOOGLE_CLIENT_ID';
const API_BASE_URL = 'http://localhost:8080/api';

function MatchList() {
  const [matches, setMatches] = useState([]);
  const [token, setToken] = useState(localStorage.getItem('google_token'));
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const login = useGoogleLogin({
    onSuccess: (tokenResponse) => {
      const accessToken = tokenResponse.access_token;
      setToken(accessToken);
      localStorage.setItem('google_token', accessToken);
      fetchMatches(accessToken);
    },
    onError: () => {
      setError('Login failed. Please try again.');
    },
  });

  const fetchMatches = async (accessToken) => {
    setLoading(true);
    setError(null);

    try {
      const response = await fetch(`${API_BASE_URL}/matches`, {
        headers: {
          'Authorization': `Bearer ${accessToken}`,
        },
      });

      if (response.ok) {
        const data = await response.json();
        setMatches(data);
      } else if (response.status === 401) {
        setError('Authentication failed. Please login again.');
        localStorage.removeItem('google_token');
        setToken(null);
      } else {
        setError('Failed to fetch matches');
      }
    } catch (err) {
      setError('Network error: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (token) {
      fetchMatches(token);
    }
  }, [token]);

  if (!token) {
    return (
      <div>
        <h1>Please login to view matches</h1>
        <button onClick={login}>Sign in with Google</button>
      </div>
    );
  }

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div>
      <h1>Matches</h1>
      <button onClick={() => fetchMatches(token)}>Refresh</button>
      <button onClick={() => {
        localStorage.removeItem('google_token');
        setToken(null);
      }}>Logout</button>
      
      <ul>
        {matches.map((match) => (
          <li key={match.matchNumber}>
            <strong>Match {match.matchNumber}</strong> - Round {match.roundNumber}<br />
            {match.homeTeam} vs {match.awayTeam}<br />
            {match.location} - {match.dateTime}<br />
            Result: {match.result || 'TBD'}
          </li>
        ))}
      </ul>
    </div>
  );
}

function App() {
  return (
    <GoogleOAuthProvider clientId={GOOGLE_CLIENT_ID}>
      <MatchList />
    </GoogleOAuthProvider>
  );
}

export default App;
```

---

## Running the Application

### Prerequisites

1. Java 21 installed
2. Maven 3.6+ installed (or use included wrapper)
3. Google Cloud project configured
4. Service account credentials downloaded
5. Google Spreadsheet shared with service account

### Steps

#### 1. Clone/Download the Project

```bash
cd /Users/marianjarembinsky/IdeaProjects/betapi
```

#### 2. Add Credentials

Place your `credentials.json` file in:
```
src/main/resources/credentials.json
```

#### 3. Update Configuration

Edit `src/main/resources/application.properties`:
- Replace `YOUR_SPREADSHEET_ID_HERE` with your actual spreadsheet ID
- Adjust the range if needed

#### 4. Build the Project

```bash
./mvnw clean install
```

#### 5. Run the Application

```bash
./mvnw spring-boot:run
```

Or build and run the JAR:
```bash
./mvnw clean package
java -jar target/betapi-0.0.1-SNAPSHOT.jar
```

#### 6. Verify the Application

The application should start on `http://localhost:8080`

Check logs for:
```
Started BetapiApplication in X.XXX seconds
```

---

## Testing

### Test with cURL

#### 1. Get a Google Access Token

You can use Google OAuth2 Playground: https://developers.google.com/oauthplayground/

1. Click the gear icon → Check "Use your own OAuth credentials"
2. Enter your Client ID and Client Secret
3. Select scopes: `openid`, `email`, `profile`
4. Click "Authorize APIs"
5. Exchange authorization code for tokens
6. Copy the "Access token"

#### 2. Test the API

```bash
# Replace {YOUR_TOKEN} with actual token
TOKEN="YOUR_GOOGLE_ACCESS_TOKEN"

# Get all matches
curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8080/api/matches

# Get specific match
curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8080/api/matches/1

# Get matches by round
curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8080/api/matches/round/1
```

### Test without Authentication (for development)

If you need to temporarily disable authentication for testing:

1. Comment out the security in `SecurityConfig.java`:
```java
.anyRequest().permitAll()  // Instead of .authenticated()
```

2. Restart the application

3. Test without token:
```bash
curl http://localhost:8080/api/matches
```

**⚠️ Remember to re-enable authentication before deployment!**

---

## Troubleshooting

### Common Issues

#### 1. "The Application Default Credentials are not available"

**Solution**: Ensure `credentials.json` is in the correct location and the path in `application.properties` is correct.

#### 2. "403 Forbidden - The caller does not have permission"

**Solution**: Make sure you've shared the Google Spreadsheet with the service account email.

#### 3. "401 Unauthorized" when calling API

**Solution**: 
- Verify your Google OAuth token is valid
- Check token expiration (Google tokens expire after 1 hour)
- Ensure the issuer-uri is correct in `application.properties`

#### 4. "No data found in spreadsheet"

**Solution**:
- Verify the spreadsheet ID is correct
- Check the range in `application.properties`
- Ensure the sheet has data

#### 5. "Failed to parse datetime"

**Solution**: Check that your dates in the spreadsheet match one of the supported formats listed in the service documentation.

---

## Security Considerations

### Production Deployment

1. **Never commit credentials.json**: Already in `.gitignore`
2. **Use environment variables**: 
   ```properties
   google.spreadsheet.id=${GOOGLE_SPREADSHEET_ID}
   google.credentials.file.path=${GOOGLE_CREDENTIALS_PATH}
   ```
3. **Enable HTTPS**: Use SSL certificates
4. **Restrict CORS**: Update `@CrossOrigin` to specific domains
5. **Add rate limiting**: Prevent API abuse
6. **Monitor logs**: Track unauthorized access attempts
7. **Regular key rotation**: Update service account keys periodically

---

## Summary

### What Was Implemented

1. ✅ **Google Sheets Integration**: Service account-based access to read spreadsheet data
2. ✅ **Data Model**: Match entity with all required fields
3. ✅ **REST API**: Three endpoints for retrieving match data
4. ✅ **OAuth2 Security**: Google JWT token validation for API access
5. ✅ **Error Handling**: Graceful handling of parsing and API errors
6. ✅ **CORS Support**: Enabled for frontend integration
7. ✅ **Logging**: Comprehensive logging for debugging and monitoring

### Architecture Flow

```
Frontend (React/Vue/Angular)
    ↓ (Google OAuth Login)
Google OAuth Server
    ↓ (JWT Token)
Frontend
    ↓ (HTTP Request + Bearer Token)
BetAPI Spring Boot
    ↓ (Validates JWT)
Spring Security
    ↓ (If valid)
MatchController
    ↓
GoogleSheetsService
    ↓ (Service Account Auth)
Google Sheets API
    ↓
Your Spreadsheet
```

### Next Steps

1. Set up your Google Cloud project
2. Create and download service account credentials
3. Update `application.properties` with your spreadsheet ID
4. Share spreadsheet with service account
5. Run the application
6. Integrate with your frontend
7. Test the complete flow
8. Deploy to production

---

## Contact & Support

For issues or questions:
- Check Spring Boot logs
- Verify Google Cloud Console configuration
- Review this documentation
- Check Google Sheets API quotas

Happy coding! 🚀

