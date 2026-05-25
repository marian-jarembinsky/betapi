# Sample Google Spreadsheet Format

## Spreadsheet Structure

Create a Google Spreadsheet with the following structure:

### Sheet Name: Sheet1

| Match# | Round# | DateTime | Location | Home Team | Away Team | Result |
|--------|--------|----------|----------|-----------|-----------|--------|
| 1 | 1 | 2025-01-15 18:00 | Emirates Stadium | Arsenal | Liverpool | 2-1 |
| 2 | 1 | 2025-01-15 20:00 | Old Trafford | Manchester United | Chelsea | 1-1 |
| 3 | 1 | 2025-01-16 15:00 | Etihad Stadium | Manchester City | Tottenham | 3-0 |
| 4 | 1 | 2025-01-16 17:30 | Anfield | Liverpool | Arsenal | TBD |
| 5 | 2 | 2025-01-22 18:00 | Stamford Bridge | Chelsea | Manchester City | TBD |
| 6 | 2 | 2025-01-22 20:00 | Tottenham Stadium | Tottenham | Manchester United | TBD |
| 7 | 2 | 2025-01-23 15:00 | Emirates Stadium | Arsenal | Chelsea | TBD |
| 8 | 2 | 2025-01-23 17:30 | Old Trafford | Manchester United | Liverpool | TBD |

## Column Descriptions

1. **Match#** (Integer)
   - Unique identifier for each match
   - Sequential numbers starting from 1
   - Required field

2. **Round#** (Integer)
   - Round or gameweek number
   - Groups matches into rounds
   - Required field

3. **DateTime** (DateTime)
   - When the match takes place
   - Supported formats:
     - `yyyy-MM-dd HH:mm:ss` (e.g., `2025-01-15 18:00:00`)
     - `yyyy-MM-dd HH:mm` (e.g., `2025-01-15 18:00`)
     - `dd/MM/yyyy HH:mm:ss` (e.g., `15/01/2025 18:00:00`)
     - `dd/MM/yyyy HH:mm` (e.g., `15/01/2025 18:00`)
   - Can be empty for future matches

4. **Location** (String)
   - Stadium or venue name
   - Free text
   - Can be empty

5. **Home Team** (String)
   - Name of the home team
   - Free text
   - Required for meaningful data

6. **Away Team** (String)
   - Name of the away team
   - Free text
   - Required for meaningful data

7. **Result** (String)
   - Match score (e.g., "2-1", "0-0")
   - "TBD" or empty for future matches
   - Free text format

## Tips for Your Spreadsheet

### 1. Header Row
Always include a header row (row 1) with column names. The application will skip this row.

### 2. Date Format
Use a consistent date format throughout. We recommend: `yyyy-MM-dd HH:mm`

Example:
- ✅ `2025-01-15 18:00`
- ❌ `Jan 15, 2025 6:00 PM`

### 3. Empty Cells
Empty cells are handled gracefully:
- Result can be "TBD" or empty for future matches
- Location can be empty
- DateTime can be empty if not yet scheduled

### 4. Data Validation (Optional but Recommended)

In Google Sheets, you can add data validation:
- **Match#**: Data validation → Number → Greater than 0
- **Round#**: Data validation → Number → Greater than 0
- **DateTime**: Data validation → Date → Is valid date

### 5. Range Configuration

In `application.properties`, set the range to cover all your data:

```properties
# For 100 matches (rows 1-101 including header)
google.spreadsheet.range=Sheet1!A1:G101

# For 50 matches
google.spreadsheet.range=Sheet1!A1:G51

# If you use a different sheet name
google.spreadsheet.range=Season2025!A1:G100
```

## Example Data Sets

### Soccer/Football League

```
1 | 1 | 2025-01-15 18:00 | Emirates Stadium | Arsenal | Liverpool | 2-1
2 | 1 | 2025-01-15 20:00 | Old Trafford | Man United | Chelsea | 1-1
3 | 1 | 2025-01-16 15:00 | Etihad Stadium | Man City | Tottenham | 3-0
```

### Basketball League

```
1 | 1 | 2025-01-20 19:00 | Madison Square Garden | Knicks | Lakers | 105-98
2 | 1 | 2025-01-20 22:00 | Chase Center | Warriors | Celtics | 112-109
3 | 2 | 2025-01-22 19:30 | TD Garden | Celtics | 76ers | TBD
```

### Tennis Tournament

```
1 | QF | 2025-01-25 14:00 | Centre Court | Djokovic | Federer | 6-4, 6-3
2 | QF | 2025-01-25 16:00 | Court 1 | Nadal | Murray | 7-6, 6-2
3 | SF | 2025-01-27 14:00 | Centre Court | Djokovic | Nadal | TBD
```

## Creating Your Spreadsheet

### Method 1: Manual Entry

1. Go to [Google Sheets](https://sheets.google.com/)
2. Click "Blank" to create new spreadsheet
3. Add the header row
4. Enter your data
5. Share with service account email

### Method 2: Import from CSV

1. Create a CSV file with your data
2. Go to Google Sheets
3. File → Import → Upload
4. Select your CSV file
5. Choose "Replace spreadsheet"
6. Share with service account email

### Method 3: Copy Template

You can make a copy of a template:
1. Create a spreadsheet with the header row
2. File → Make a copy
3. Share the copy with your service account

## Spreadsheet ID

After creating your spreadsheet, get the ID from the URL:

```
https://docs.google.com/spreadsheets/d/1aBC_def2GHI3jkl4MNO5pqr6STU/edit
                                      ↑
                                This is your Spreadsheet ID
```

Copy this ID to your `application.properties`:

```properties
google.spreadsheet.id=1aBC_def2GHI3jkl4MNO5pqr6STU
```

## Testing Your Spreadsheet

Before connecting the API, verify:
- ✅ Header row is present
- ✅ Data starts from row 2
- ✅ Dates are in a supported format
- ✅ No completely empty rows in the middle
- ✅ Spreadsheet is shared with service account email

---

Happy data organizing! 📊

