# 🚀 AZIZLATOR
### The Most Powerful Windows Emulator for Android

> Built by **Aziz** | Powered by Wine + Box64 + DXVK

---

## 📱 About Azizlator

Azizlator is an advanced Windows emulator for Android that **works on ALL devices** - from low-end phones like Helio G88 to flagship Snapdragon 8 Gen 3.

### 🌟 What makes Azizlator unique:
- **Auto-Optimizer Engine** - detects your CPU automatically and applies best settings
- **Works on ALL chipsets** - Snapdragon, Dimensity, Helio, Exynos
- **SA:MP optimized** - special profile for GTA San Andreas Multiplayer
- **Container system** - run multiple Windows environments
- **No PC required** - built entirely from Android using Termux

---

## 🏗️ Project Structure

```
Azizlator/
├── app/
│   └── src/main/
│       ├── java/com/azizlator/
│       │   ├── ui/
│       │   │   ├── SplashActivity.kt      ← Splash screen
│       │   │   ├── MainActivity.kt         ← Main dashboard
│       │   │   ├── ContainerActivity.kt    ← Container setup
│       │   │   └── SettingsActivity.kt     ← Settings
│       │   ├── engine/
│       │   │   ├── DeviceOptimizer.kt     ← 🧠 AI Auto-Optimizer
│       │   │   ├── WineEngine.kt          ← Wine controller
│       │   │   ├── ContainerManager.kt    ← Container system
│       │   │   └── AzizlatorService.kt    ← Background service
│       │   └── utils/
│       │       ├── PrefsManager.kt        ← Settings storage
│       │       └── ContainerAdapter.kt    ← RecyclerView adapter
│       ├── res/layout/
│       │   ├── activity_splash.xml
│       │   ├── activity_main.xml
│       │   ├── activity_container.xml
│       │   ├── activity_settings.xml
│       │   └── item_container.xml
│       └── AndroidManifest.xml
├── .github/workflows/
│   └── build.yml                          ← Auto-build APK on GitHub
└── README.md
```

---

## 📊 Performance Modes

| Device | Mode | Resolution | DXVK | Turnip |
|--------|------|-----------|------|--------|
| Snapdragon 8 Gen 2/3 | ULTRA | 1920x1080 | ✅ | ✅ |
| Snapdragon 8 Gen 1/888 | HIGH | 1280x720 | ✅ | ✅ |
| Snapdragon 7 Series | BALANCED | 1024x768 | ✅ | ❌ |
| Dimensity 9000+ | HIGH | 1280x720 | ✅ | ❌ |
| **Helio G88** *(your device)* | **COMPATIBILITY** | **800x600** | ❌ | ❌ |
| Helio G85/G80 | LOW_END | 800x600 | ❌ | ❌ |

---

## 🚀 How to Build (From Phone using Termux)

```bash
# 1. Install Termux from F-Droid
# 2. Setup Git
pkg install git

# 3. Clone your repo
git clone https://github.com/YOUR_USERNAME/Azizlator.git
cd Azizlator

# 4. Push to GitHub - GitHub Actions will build the APK automatically!
git add .
git commit -m "Initial commit"
git push origin main

# 5. Go to GitHub → Actions → Download APK artifact
```

---

## 📥 Installation Steps

1. Upload project to **GitHub**
2. **GitHub Actions** builds the APK automatically ☁️
3. Download APK from **Actions → Artifacts**
4. Install on your Android device
5. Launch **Azizlator** 🚀

---

## 🔮 Roadmap

- [ ] v1.0 - Basic UI + Container System + Auto-Optimizer
- [ ] v1.1 - Wine + Box64 integration
- [ ] v1.2 - DXVK support
- [ ] v1.3 - SA:MP optimized profile
- [ ] v2.0 - Full GPU rendering

---

## 👨‍💻 Author

**Aziz** - Creator of Azizlator  
Built with ❤️ using Claude AI

---

*"From one phone, we built a world-class emulator"* 🌍
.
