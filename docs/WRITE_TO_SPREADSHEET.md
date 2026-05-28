# Writing to Google Spreadsheet

This guide explains what you need to do to enable **write access** to your Google Spreadsheet.

---

## What Changed in the Code

| File | Change |
|------|--------|
| `GoogleSheetsConfig.java` | Scope changed from `SPREADSHEETS_READONLY` → `SPREADSHEETS` |
| `GoogleSheetsService.java` | Added `updateResult(matchNumber, result)` method |
| `MatchController.java` | Added `PUT /api/matches/{matchNumber}/result` endpoint |
| `UpdateResultRequest.java` | New DTO with `result` field |

---

## Step 1 — Change Service Account Role in Google Cloud Console

Your service account currently has **Viewer** role on the spreadsheet. You need to upgrade it to **Editor**.

1. Open your Google Spreadsheet
2. Click **Share** (top right)
3. Find your service account email (e.g. `betapi-sheets-reader@your-project.iam.gserviceaccount.com`)
4. Change its role from **Viewer** → **Editor**
5. Click **Save**

> ⚠️ Without this step, all write calls will return **403 Forbidden** from Google Sheets API.

---

## Step 2 — Verify the Result Column Index

The `updateResult` method writes to **column H** (index 7) which is the `result` column.

Make sure your spreadsheet columns are in this order:
```
A            B        C     D         E          F          G      H
Match Number | Round | Date | Location | Home Team | Away Team | Group | Result
```

If your `Result` column is in a different position, update the cell range in `GoogleSheetsService.java`:
```java
// Change H to the correct column letter
String cellRange = "Sheet1!H" + sheetRowNumber;
```

Also update the read range in `application.properties` to include column H:
```properties
google.spreadsheet.range=Sheet1!A1:H100
```

---

## Step 3 — Test the Endpoint

### Using Bruno

Create a new file `update-result.yml` in your Bruno collection:

```yaml
info:
  name: update-result
  type: http
  seq: 5

http:
  method: PUT
  url: {{baseUrl}}/api/matches/1/result
  headers:
    - name: Authorization
      value: Bearer {{bearerToken}}
    - name: Content-Type
      value: application/json
  body:
    mode: json
    json: |
      {
        "result": "2:1"
      }
  auth: inherit

settings:
  encodeUrl: true
  timeout: 0
  followRedirects: true
  maxRedirects: 5
```

Replace `1` in the URL with the actual match number you want to update.

### Using cURL

```bash
curl -X PUT http://localhost:8080/api/matches/1/result \
  -H "Authorization: Bearer YOUR_ID_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"result": "2:1"}'
```

---

## How It Works

1. The endpoint receives `matchNumber` from the URL path and `result` from the request body
2. The service fetches all rows from the spreadsheet
3. It finds the row where column A (Match Number) matches the requested `matchNumber`
4. It calculates the exact cell — e.g. match at row index 5 → sheet row 6 → cell `H6`
5. It writes the result value to that cell using `ValueInputOption=RAW`

---

## Error Responses

| HTTP Status | Reason |
|-------------|--------|
| `200 OK` | Result updated successfully |
| `401 Unauthorized` | Missing or expired Bearer token |
| `403 Forbidden` | Service account doesn't have Editor role on spreadsheet |
| `500 Internal Server Error` | Match number not found, or Google Sheets API error |

---

## Result Format Examples

You can write any string as a result:
- `"2:1"` — final score
- `"1:1 (AET)"` — after extra time
- `"3:2 (PSO)"` — after penalty shootout
- `"postponed"` — match not played yet

