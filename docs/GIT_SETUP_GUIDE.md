# Git Setup and First Commit Guide

This guide will help you set up Git remote and push your first commit for the BetAPI project.

## Prerequisites

- Git installed on your computer
- GitHub/GitLab/Bitbucket account
- Terminal/Command Line access

---

## Step 1: Initialize Git Repository (if not already done)

Check if Git is already initialized:
```bash
cd /Users/marianjarembinsky/IdeaProjects/betapi
git status
```

If Git is not initialized, run:
```bash
git init
```

---

## Step 2: Create Remote Repository

### Option A: GitHub

1. Go to [GitHub](https://github.com)
2. Click the "+" icon → "New repository"
3. Repository name: `betapi`
4. Description: "Spring Boot REST API for reading sports match data from Google Sheets"
5. Choose: **Public** or **Private**
6. **DO NOT** initialize with README, .gitignore, or license (we already have these)
7. Click "Create repository"
8. Copy the repository URL (e.g., `https://github.com/yourusername/betapi.git`)

### Option B: GitLab

1. Go to [GitLab](https://gitlab.com)
2. Click "New project" → "Create blank project"
3. Project name: `betapi`
4. Uncheck "Initialize repository with a README"
5. Click "Create project"
6. Copy the repository URL

### Option C: Bitbucket

1. Go to [Bitbucket](https://bitbucket.org)
2. Click "Create" → "Repository"
3. Repository name: `betapi`
4. Uncheck "Include a README"
5. Click "Create repository"
6. Copy the repository URL

---

## Step 3: Configure Git (First Time Only)

If you haven't configured Git before, set your identity:

```bash
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

Verify configuration:
```bash
git config --global --list
```

---

## Step 4: Review Files to Commit

Check what will be committed:
```bash
cd /Users/marianjarembinsky/IdeaProjects/betapi
git status
```

You should see:
- ✅ All source files (.java)
- ✅ pom.xml
- ✅ Documentation files (.md)
- ✅ .gitignore
- ❌ credentials.json (should be ignored)
- ❌ target/ directory (should be ignored)

---

## Step 5: Stage Files for Commit

Add all files to staging:
```bash
git add .
```

Or add specific files:
```bash
git add pom.xml
git add src/
git add *.md
git add .gitignore
git add mvnw mvnw.cmd .mvn/
```

Verify what's staged:
```bash
git status
```

**Important**: Make sure `credentials.json` is NOT in the list!

---

## Step 6: Create First Commit

Commit the staged files:
```bash
git commit -m "Initial commit: BetAPI with Google Sheets integration

- Added Spring Boot application with Java 21
- Implemented Google Sheets API integration
- Added OAuth2 security with JWT validation
- Created REST API endpoints for match data
- Added Match model and services
- Included comprehensive documentation
- Set up security configurations"
```

Or a simpler commit message:
```bash
git commit -m "Initial commit: BetAPI project setup"
```

---

## Step 7: Add Remote Repository

Add your remote repository URL:

### For HTTPS:
```bash
git remote add origin https://github.com/yourusername/betapi.git
```

### For SSH:
```bash
git remote add origin git@github.com:yourusername/betapi.git
```

Replace `yourusername` with your actual username and adjust the platform (GitHub/GitLab/Bitbucket).

Verify remote was added:
```bash
git remote -v
```

---

## Step 8: Push to Remote Repository

### First Time Push (Create Main Branch)

```bash
git branch -M main
git push -u origin main
```

This will:
- Rename your branch to `main` (if it's `master`)
- Push your code to the remote repository
- Set up tracking between local and remote branches

### Enter Credentials

You may be prompted for credentials:

**HTTPS**: Enter your username and password/token
**SSH**: Make sure your SSH key is added to your GitHub/GitLab/Bitbucket account

---

## Step 9: Verify Push

Check your remote repository on GitHub/GitLab/Bitbucket. You should see:
- All your source files
- Documentation files
- pom.xml
- NO credentials.json (should be ignored)

---

## Git Workflow for Future Changes

### 1. Make Changes
Edit your files as needed

### 2. Check Status
```bash
git status
```

### 3. Stage Changes
```bash
# Stage all changes
git add .

# Or stage specific files
git add src/main/java/com/example/betapi/controller/MatchController.java
```

### 4. Commit Changes
```bash
git commit -m "Brief description of changes"
```

### 5. Push to Remote
```bash
git push
```

---

## Common Git Commands

| Command | Description |
|---------|-------------|
| `git status` | Check current status |
| `git add <file>` | Stage specific file |
| `git add .` | Stage all changes |
| `git commit -m "message"` | Commit with message |
| `git push` | Push to remote |
| `git pull` | Pull from remote |
| `git log` | View commit history |
| `git log --oneline` | View compact history |
| `git branch` | List branches |
| `git branch <name>` | Create new branch |
| `git checkout <branch>` | Switch branch |
| `git checkout -b <name>` | Create and switch to branch |
| `git merge <branch>` | Merge branch |

---

## Example: Complete First-Time Setup

Here's a complete example from start to finish:

```bash
# Navigate to project
cd /Users/marianjarembinsky/IdeaProjects/betapi

# Initialize Git (if not already done)
git init

# Check status
git status

# Stage all files
git add .

# Verify staging (credentials.json should NOT be listed)
git status

# Create first commit
git commit -m "Initial commit: BetAPI with Google Sheets integration"

# Add remote (replace with your URL)
git remote add origin https://github.com/yourusername/betapi.git

# Rename branch to main
git branch -M main

# Push to remote
git push -u origin main

# Enter credentials if prompted

# Verify on GitHub/GitLab/Bitbucket
```

---

## Branching Strategy (Optional but Recommended)

### Main Branch Protection

Keep `main` branch stable:
- Create feature branches for new work
- Test before merging to main
- Use pull requests for code review

### Feature Branch Workflow

1. **Create Feature Branch**:
   ```bash
   git checkout -b feature/add-match-filtering
   ```

2. **Make Changes and Commit**:
   ```bash
   git add .
   git commit -m "Add filtering by team name"
   ```

3. **Push Feature Branch**:
   ```bash
   git push -u origin feature/add-match-filtering
   ```

4. **Create Pull Request** on GitHub/GitLab/Bitbucket

5. **Merge to Main** after review

6. **Delete Feature Branch**:
   ```bash
   git checkout main
   git branch -d feature/add-match-filtering
   ```

---

## .gitignore Configuration

Your `.gitignore` already includes:

```gitignore
# Credentials and sensitive files
src/main/resources/credentials.json
credentials.json
application-local.properties

# Maven
../target/
!.mvn/wrapper/maven-wrapper.jar

# IDE
.idea/
*.iws
*.iml
*.ipr
.vscode/
.DS_Store

# Logs
*.log
logs/

# Spring Boot
spring-output/
```

**Important**: Never commit sensitive files like:
- ❌ credentials.json
- ❌ API keys
- ❌ Passwords
- ❌ OAuth client secrets

---

## Adding Credentials Safely (Advanced)

If you need to share credentials with team members:

### Option 1: Environment Variables
```properties
# application.properties
google.credentials.file.path=${GOOGLE_CREDENTIALS_PATH}
google.spreadsheet.id=${GOOGLE_SPREADSHEET_ID}
```

### Option 2: Git Secrets Manager
Use services like:
- GitHub Secrets
- GitLab CI/CD Variables
- AWS Secrets Manager
- HashiCorp Vault

### Option 3: Encrypted Secrets
Use tools like:
- git-crypt
- BlackBox
- SOPS (Secrets OPerationS)

---

## README Update Suggestion

After pushing, update your GitHub/GitLab repository settings:

1. **Add description**: "Spring Boot REST API for reading sports match data from Google Sheets"
2. **Add topics/tags**: `spring-boot`, `google-sheets`, `oauth2`, `rest-api`, `java`
3. **Enable Issues**: For tracking bugs and features
4. **Set up GitHub Actions/GitLab CI** (optional): For automated testing

---

## Troubleshooting

### "fatal: not a git repository"
```bash
git init
```

### "error: remote origin already exists"
```bash
git remote remove origin
git remote add origin <your-url>
```

### "Permission denied (publickey)"
- Set up SSH key: https://docs.github.com/en/authentication/connecting-to-github-with-ssh
- Or use HTTPS instead of SSH

### "Updates were rejected"
```bash
# Pull first, then push
git pull origin main --rebase
git push origin main
```

### Accidentally committed credentials.json
```bash
# Remove from Git but keep file
git rm --cached src/main/resources/credentials.json
git commit -m "Remove credentials.json from Git"
git push

# If already pushed, rotate your credentials immediately!
```

---

## Quick Reference Commands

### Initial Setup
```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin <url>
git branch -M main
git push -u origin main
```

### Daily Workflow
```bash
git status
git add .
git commit -m "Description"
git push
```

### Undo Changes
```bash
# Undo unstaged changes
git checkout -- <file>

# Unstage file
git reset HEAD <file>

# Undo last commit (keep changes)
git reset --soft HEAD~1

# Undo last commit (discard changes) - CAREFUL!
git reset --hard HEAD~1
```

---

## Next Steps After First Push

1. ✅ Add repository description on GitHub/GitLab
2. ✅ Add topics/tags
3. ✅ Set up branch protection rules (optional)
4. ✅ Add collaborators (if team project)
5. ✅ Set up CI/CD pipeline (optional)
6. ✅ Create project board for task tracking (optional)

---

## Congratulations! 🎉

Your BetAPI project is now version controlled and backed up on a remote repository!

**What you've accomplished**:
- ✅ Initialized Git repository
- ✅ Created first commit
- ✅ Set up remote repository
- ✅ Pushed code to remote
- ✅ Protected sensitive files

**You can now**:
- 📝 Track changes over time
- 🔄 Collaborate with others
- 🔙 Revert to previous versions
- 🌿 Work on multiple features simultaneously
- ☁️ Back up your code in the cloud

Happy coding! 🚀

