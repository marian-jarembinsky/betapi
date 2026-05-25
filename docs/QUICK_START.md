# Quick Start Checklist

Follow these steps to get BetAPI up and running:

## ✅ Step-by-Step Setup

### 1. Google Cloud Console Setup (15 minutes)

- [ ] Go to [Google Cloud Console](https://console.cloud.google.com/)
- [ ] Create a new project named "BetAPI"
- [ ] Enable Google Sheets API
- [ ] Create a Service Account named "betapi-sheets-reader"
- [ ] Create and download JSON key for the service account
- [ ] Rename the downloaded key to `credentials.json`
- [ ] Move `credentials.json` to `src/main/resources/credentials.json`
- [ ] Copy the service account email (e.g., `betapi-sheets-reader@...iam.gserviceaccount.com`)

### 2. Google Spreadsheet Setup (5 minutes)

- [ ] Create or open your Google Spreadsheet with match data
- [ ] Format the spreadsheet with these columns:
  ```
  Match# | Round# | DateTime | Location | Home Team | Away Team | Result
  ```
- [ ] Add sample data (see SAMPLE_SPREADSHEET.md for examples)
- [ ] Click "Share" button
- [ ] Add the service account email from step 1
- [ ] Grant "Viewer" permission
- [ ] Copy the Spreadsheet ID from the URL

### 3. Application Configuration (3 minutes)

- [ ] Open `src/main/resources/application.properties`
- [ ] Replace `YOUR_SPREADSHEET_ID_HERE` with your Spreadsheet ID
- [ ] Adjust the range if needed (default: `Sheet1!A1:G100`)
- [ ] Save the file

### 4. Build and Run (2 minutes)

- [ ] Open terminal in project directory
- [ ] Run: `./mvnw clean install`
- [ ] Wait for build to complete
- [ ] Run: `./mvnw spring-boot:run`
- [ ] Wait for "Started BetapiApplication" message
- [ ] Verify server is running on http://localhost:8080

### 5. OAuth2 Setup for Frontend (10 minutes)

- [ ] Go to [Google Cloud Console](https://console.cloud.google.com/)
- [ ] Navigate to APIs & Services → Credentials
- [ ] Configure OAuth consent screen
  - User Type: External
  - App name: BetAPI
  - Add your email
- [ ] Create OAuth Client ID
  - Type: Web application
  - Name: BetAPI Frontend
  - Authorized origins: `http://localhost:3000`
  - Authorized redirect URIs: `http://localhost:3000/callback`
- [ ] Copy the Client ID for your frontend

### 6. Test the API (5 minutes)

- [ ] Go to [Google OAuth Playground](https://developers.google.com/oauthplayground/)
- [ ] Click gear icon → "Use your own OAuth credentials"
- [ ] Enter your Client ID and Client Secret
- [ ] Select scopes: `openid`, `email`, `profile`
- [ ] Authorize and get access token
- [ ] Test with cURL:
  ```bash
  curl -H "Authorization: Bearer YOUR_TOKEN" \
       http://localhost:8080/api/matches
  ```
- [ ] Verify you receive JSON response with match data

### 7. Frontend Integration

- [ ] Install OAuth library in your frontend:
  ```bash
  npm install @react-oauth/google
  ```
- [ ] Implement Google login (see GOOGLE_SHEETS_INTEGRATION_GUIDE.md)
- [ ] Call API with Bearer token
- [ ] Display match data

## 🎉 Success Indicators

You'll know everything is working when:
- ✅ Application starts without errors
- ✅ You can authenticate with Google
- ✅ API returns match data from your spreadsheet
- ✅ Frontend can fetch and display matches

## 🆘 Common Issues

**"The Application Default Credentials are not available"**
→ Check that `credentials.json` exists in `src/main/resources/`

**"403 Forbidden"**
→ Make sure spreadsheet is shared with service account email

**"401 Unauthorized"**
→ Verify your Google OAuth token is valid

**"No data found"**
→ Check spreadsheet ID and range in application.properties

## 📚 Next Steps

- Read the complete guide: [GOOGLE_SHEETS_INTEGRATION_GUIDE.md](GOOGLE_SHEETS_INTEGRATION_GUIDE.md)
- Customize the Match model if needed
- Add more endpoints
- Deploy to production
- Set up CI/CD pipeline

---

**Total Setup Time: ~40 minutes**

Good luck! 🚀

