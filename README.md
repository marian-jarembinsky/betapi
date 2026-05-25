# BetAPI

A Spring Boot REST API that reads sports match data from Google Sheets and exposes it through secure OAuth2-protected endpoints.

## 🎯 Features

- **Google Sheets Integration**: Read sports match data directly from Google Spreadsheets
- **OAuth2 Security**: Secure API endpoints with Google JWT authentication
- **REST API**: Clean RESTful endpoints returning JSON data
- **Match Management**: View all matches, filter by match number or round
- **Error Handling**: Robust error handling and logging
- **CORS Enabled**: Ready for frontend integration

## 📋 Prerequisites

- Java 21 or higher
- Maven 3.6+ (or use the included Maven wrapper)
- Google Cloud account
- Google Spreadsheet with match data

## 🚀 Quick Start

### 1. Google Cloud Setup

Follow the detailed guide in [GOOGLE_SHEETS_INTEGRATION_GUIDE.md](docs/GOOGLE_SHEETS_INTEGRATION_GUIDE.md) to:
- Create a Google Cloud project
- Enable Google Sheets API
- Create service account and download credentials
- Set up OAuth2 for frontend authentication

### 2. Configure the Application

1. Place your Google service account credentials in:
   ```
   src/main/resources/credentials.json
   ```

2. Update `src/main/resources/application.properties`:
   ```properties
   google.spreadsheet.id=YOUR_SPREADSHEET_ID
   google.spreadsheet.range=Sheet1!A1:G100
   ```

3. Share your Google Spreadsheet with the service account email (found in credentials.json)

### 3. Build and Run

```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

## 📊 Google Spreadsheet Format

Your spreadsheet should have the following columns (with header row):

| Match# | Round# | DateTime | Location | Home Team | Away Team | Result |
|--------|--------|----------|----------|-----------|-----------|--------|
| 1 | 1 | 2025-01-15 18:00 | Stadium A | Team A | Team B | 2-1 |
| 2 | 1 | 2025-01-15 20:00 | Stadium B | Team C | Team D | TBD |

## 🔌 API Endpoints

All endpoints require Google OAuth2 Bearer token authentication.

### Get All Matches
```http
GET /api/matches
Authorization: Bearer {google_jwt_token}
```

### Get Match by Number
```http
GET /api/matches/{matchNumber}
Authorization: Bearer {google_jwt_token}
```

### Get Matches by Round
```http
GET /api/matches/round/{roundNumber}
Authorization: Bearer {google_jwt_token}
```

### Response Example
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
  }
]
```

## 🔐 Frontend Integration

Example using React and @react-oauth/google:

```javascript
import { useGoogleLogin } from '@react-oauth/google';

const login = useGoogleLogin({
  onSuccess: async (tokenResponse) => {
    const response = await fetch('http://localhost:8080/api/matches', {
      headers: {
        'Authorization': `Bearer ${tokenResponse.access_token}`
      }
    });
    const matches = await response.json();
    console.log(matches);
  }
});
```

See [GOOGLE_SHEETS_INTEGRATION_GUIDE.md](docs/GOOGLE_SHEETS_INTEGRATION_GUIDE.md) for complete frontend examples.

## 🏗️ Project Structure

```
betapi/
├── src/
│   ├── main/
│   │   ├── java/com/example/betapi/
│   │   │   ├── config/
│   │   │   │   ├── GoogleSheetsConfig.java    # Google Sheets API configuration
│   │   │   │   └── SecurityConfig.java         # OAuth2 security configuration
│   │   │   ├── controller/
│   │   │   │   └── MatchController.java        # REST endpoints
│   │   │   ├── model/
│   │   │   │   └── Match.java                  # Match entity
│   │   │   ├── service/
│   │   │   │   └── GoogleSheetsService.java    # Google Sheets integration
│   │   │   └── BetapiApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── credentials.json                # Google service account key
│   └── test/
├── pom.xml
├── README.md
└── GOOGLE_SHEETS_INTEGRATION_GUIDE.md         # Detailed setup guide
```

## 🛠️ Technology Stack

- **Spring Boot 4.0.0** - Application framework
- **Spring Security** - OAuth2 resource server
- **Google Sheets API v4** - Data source integration
- **Lombok** - Reduce boilerplate code
- **Maven** - Dependency management
- **Java 21** - Programming language

## 📚 Documentation

For detailed setup instructions, Google Cloud configuration, security setup, and frontend integration examples, see:

**[📖 Complete Integration Guide](docs/GOOGLE_SHEETS_INTEGRATION_GUIDE.md)**

## 🧪 Testing

### Using cURL

```bash
# Get a token from Google OAuth Playground: https://developers.google.com/oauthplayground/
TOKEN="your_google_access_token"

# Test the API
curl -H "Authorization: Bearer $TOKEN" \
     http://localhost:8080/api/matches
```

### Run Tests

```bash
./mvnw test
```

## 🔒 Security Notes

- Never commit `credentials.json` to version control (already in .gitignore)
- Use environment variables for production deployment
- Google access tokens expire after 1 hour
- Always use HTTPS in production
- Restrict CORS origins for production

## 🐛 Troubleshooting

**403 Forbidden**: Make sure you've shared the spreadsheet with your service account email

**401 Unauthorized**: Verify your Google OAuth token is valid and not expired

**No data found**: Check spreadsheet ID and range in application.properties

For more troubleshooting tips, see the [Complete Integration Guide](docs/GOOGLE_SHEETS_INTEGRATION_GUIDE.md).

## 📝 License

This project is for educational/personal use.

## 🤝 Contributing

Feel free to fork and submit pull requests!

---

**Need Help?** Check the [Complete Integration Guide](docs/GOOGLE_SHEETS_INTEGRATION_GUIDE.md) for step-by-step instructions on:
- Google Cloud Console setup
- Service account configuration
- OAuth2 authentication flow
- Frontend integration examples
- Security best practices
│   └── test/
│       └── java/
│           └── com/example/betapi/
│               └── BetapiApplicationTests.java
├── pom.xml
└── README.md
```

## Technologies

- **Spring Boot 4.0.0** - Application framework
- **Java 21** - Programming language
- **Maven** - Build tool

## License

This project is licensed under the terms specified in the LICENSE file.

## Contact

For questions or support, please contact the project maintainers.

