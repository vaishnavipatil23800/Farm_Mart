# FarmMart — Spring Boot Migration Guide
# Complete Step-by-Step Instructions (Zero Java Knowledge Required)

===========================================================
  READ THIS FIRST
===========================================================

This project has TWO parts:

  1. farmmart-spring/   ← NEW Java Spring Boot backend (replaces Node.js server/)
  2. client/            ← SAME React frontend (only 1 line changed in SummaryApi.js)

You keep your MongoDB database. All your data stays the same.
The API URLs are 100% identical to your old Node.js server.

===========================================================
  PART 1: INSTALL REQUIRED SOFTWARE
===========================================================

You need to install 3 things before you can run this project.
You only do this ONCE on your computer.

─────────────────────────────────────────────────────────
STEP 1 — Install Java 21
─────────────────────────────────────────────────────────

Java is the programming language Spring Boot runs on.
(Like how Node.js is needed to run Express.js)

1. Go to: https://adoptium.net/en-GB/temurin/releases/
2. Select:
     Version:   21 (LTS)
     OS:        Your operating system (Windows / macOS / Linux)
     Arch:      x64 (most computers) or aarch64 (Apple M1/M2 Mac)
     Package:   .msi (Windows) or .pkg (Mac) or .deb (Ubuntu)
3. Download and run the installer
4. ✅ On the installer screen, make sure "Add to PATH" is checked
5. Click Install / Next through all steps

TEST IT WORKS:
  Open a new terminal and type:
    java -version
  You should see something like:
    openjdk version "21.0.3" 2024-04-16

  If you see "command not found", restart your terminal and try again.

─────────────────────────────────────────────────────────
STEP 2 — Install Maven (the build tool, like npm for Java)
─────────────────────────────────────────────────────────

Maven downloads libraries and builds the project.
(Like how npm install works for Node.js)

Option A - Windows:
  1. Go to: https://maven.apache.org/download.cgi
  2. Download "Binary zip archive" (apache-maven-3.9.x-bin.zip)
  3. Extract the zip to C:\Program Files\Maven\
  4. Add C:\Program Files\Maven\apache-maven-3.9.x\bin to your PATH:
     - Search "Environment Variables" in Windows search
     - Click "Environment Variables"
     - Under System Variables, find "Path" → Edit → New
     - Add: C:\Program Files\Maven\apache-maven-3.9.x\bin
     - Click OK on all windows

Option B - Mac (with Homebrew):
  brew install maven

Option C - Ubuntu/Debian Linux:
  sudo apt update
  sudo apt install maven

TEST IT WORKS:
  Open a NEW terminal and type:
    mvn -version
  You should see:
    Apache Maven 3.9.x ...

─────────────────────────────────────────────────────────
STEP 3 — MongoDB
─────────────────────────────────────────────────────────

You already have MongoDB running (from your Node.js project).
No changes needed here! Use the same MongoDB URI.

===========================================================
  PART 2: SET UP YOUR ENVIRONMENT VARIABLES
===========================================================

Open this file:
  farmmart-spring/src/main/resources/application.properties

Replace the placeholder values with your REAL values.
These are the SAME values from your old Node.js .env file.

Find these lines and update them:

  spring.data.mongodb.uri=YOUR_MONGODB_URI_HERE
  For example: spring.data.mongodb.uri=mongodb+srv://user:pass@cluster.mongodb.net/farmmart

  jwt.secret=YOUR_JWT_SECRET_HERE
  IMPORTANT: Must be at least 32 characters long!
  For example: jwt.secret=MyFarmMartSuperSecretKey2024ForJWT

  cloudinary.cloud-name=YOUR_CLOUDINARY_CLOUD_NAME
  cloudinary.api-key=YOUR_CLOUDINARY_API_KEY
  cloudinary.api-secret=YOUR_CLOUDINARY_API_SECRET

  resend.api-key=YOUR_RESEND_API_KEY

  frontend.url=http://localhost:5173

SAVE THE FILE after making changes.

===========================================================
  PART 3: RUN THE SPRING BOOT BACKEND
===========================================================

─────────────────────────────────────────────────────────
STEP 1 — Open terminal in the backend folder
─────────────────────────────────────────────────────────

Windows: Open File Explorer → go to farmmart-spring folder
         → hold Shift + right-click → "Open PowerShell window here"

Mac/Linux: Open Terminal → type:
  cd /path/to/farmmart-spring

─────────────────────────────────────────────────────────
STEP 2 — Download all Java dependencies (like npm install)
─────────────────────────────────────────────────────────

Type this command:
  mvn dependency:resolve

This is like "npm install" — it downloads all the Java libraries.
FIRST TIME: This takes 2-5 minutes (downloads ~50MB of libraries).
NEXT TIMES: Instant (already cached).

You'll see a lot of text scrolling. Wait for:
  BUILD SUCCESS

─────────────────────────────────────────────────────────
STEP 3 — Start the server
─────────────────────────────────────────────────────────

Type:
  mvn spring-boot:run

You'll see logs scrolling. Wait until you see:
  ====================================
   FarmMart Server is Running!
   Port: 8080
  ====================================

Your backend is now running on http://localhost:8080

─────────────────────────────────────────────────────────
STEP 4 — Test it's working
─────────────────────────────────────────────────────────

Open your browser and visit:
  http://localhost:8080/api/category/get

You should see:
  {"data":[],"error":false,"success":true}

If you see that, your backend is working! ✅

===========================================================
  PART 4: RUN THE REACT FRONTEND
===========================================================

Open a NEW terminal window (keep the backend running in the first one).

cd client
npm install
npm run dev

Visit http://localhost:5173 — your full app should work exactly as before.

===========================================================
  COMMON ERRORS AND FIXES
===========================================================

ERROR: "port 8080 already in use"
FIX: Your old Node.js server is still running. Stop it first (Ctrl+C in its terminal)
     OR change the port in application.properties: server.port=8081

ERROR: "Failed to connect to MongoDB"
FIX: Check your spring.data.mongodb.uri in application.properties
     Make sure the URI is correct and MongoDB is running

ERROR: "jwt.secret must be at least 32 characters"
FIX: Make your JWT secret longer in application.properties

ERROR: "BUILD FAILURE" with "Cannot find symbol"
FIX: Make sure you saved ALL the .java files in the correct folders.
     Run: mvn clean spring-boot:run (the "clean" clears any cached state)

ERROR: "CORS error" in browser console
FIX: Check frontend.url in application.properties matches exactly
     what's in your browser address bar (including http:// vs https://)

ERROR: "401 Unauthorized" on protected routes
FIX: Normal! Those routes require login. Test login first,
     then protected routes will work with the JWT token.

===========================================================
  DEVELOPMENT TIPS
===========================================================

• mvn spring-boot:run          ← Start the server
• mvn clean spring-boot:run    ← Clean start (if you see weird errors)
• Ctrl+C                       ← Stop the server
• The server auto-restarts when you change .java files (like nodemon)

• To check all your API routes, visit:
  http://localhost:8080/api/category/get   (public - no login needed)
  http://localhost:8080/api/product/get    (public - no login needed)

===========================================================
  FILE STRUCTURE REFERENCE
===========================================================

farmmart-spring/
├── pom.xml                                    ← Like package.json (dependencies)
├── src/
│   └── main/
│       ├── java/com/farmmart/
│       │   ├── FarmMartApplication.java       ← Like index.js (entry point)
│       │   ├── config/
│       │   │   ├── SecurityConfig.java        ← CORS + Auth + Role guards
│       │   │   ├── CloudinaryConfig.java      ← Cloudinary setup
│       │   │   └── WebClientConfig.java       ← HTTP client for email
│       │   ├── filter/
│       │   │   └── JwtAuthFilter.java         ← Like middleware/auth.js
│       │   ├── model/
│       │   │   ├── User.java                  ← Like models/user.model.js
│       │   │   ├── Product.java               ← Like models/product.model.js
│       │   │   ├── Category.java
│       │   │   ├── SubCategory.java
│       │   │   ├── CartProduct.java
│       │   │   ├── Address.java
│       │   │   └── Order.java
│       │   ├── repository/
│       │   │   ├── UserRepository.java        ← Auto-generated DB queries
│       │   │   ├── ProductRepository.java
│       │   │   ├── CategoryRepository.java
│       │   │   ├── SubCategoryRepository.java
│       │   │   ├── CartProductRepository.java
│       │   │   ├── AddressRepository.java
│       │   │   └── OrderRepository.java
│       │   ├── service/
│       │   │   ├── EmailService.java          ← Like config/sendEmail.js
│       │   │   └── CloudinaryService.java     ← Like utils/uploadImageClodinary.js
│       │   ├── util/
│       │   │   ├── JwtUtil.java               ← Like utils/generatedAccessToken.js
│       │   │   └── OtpUtil.java               ← Like utils/generatedOtp.js
│       │   └── controller/
│       │       ├── UserController.java        ← route/user.route.js + controller
│       │       ├── ProductController.java     ← route/product.route.js + controller
│       │       ├── CategoryController.java
│       │       ├── SubCategoryController.java
│       │       ├── CartController.java
│       │       ├── AddressController.java
│       │       └── UploadController.java
│       └── resources/
│           └── application.properties        ← Like .env file
└── client/                                   ← React frontend (unchanged)
    └── src/common/SummaryApi.js              ← Only this one line changed

===========================================================
  BUGS FIXED IN THIS MIGRATION
===========================================================

1. OTP expiry bug:
   Node.js (BROKEN):  new Date() + 60 * 60 * 1000  ← string concatenation!
   Spring Boot (FIXED): Instant.now().plus(1, ChronoUnit.HOURS)

2. Product update injection bug:
   Node.js (BROKEN):  ProductModel.updateOne({ ...request.body })  ← any field injectable!
   Spring Boot (FIXED): Only explicitly listed fields can be updated

3. Port variable bug:
   Node.js (BROKEN):  const PORT = 8080 || process.env.PORT  ← always 8080!
   Spring Boot (FIXED): server.port=${PORT:8080}  ← env var first, 8080 as default

4. Hardcoded localhost:
   React (FIXED): baseURL = import.meta.env.VITE_API_URL || "http://localhost:8080"
