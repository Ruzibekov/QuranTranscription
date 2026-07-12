# QuranTranscription Agent Qoidalari

- Menga doim o'zbek tilida javob ber.
- Kodda kommentariya yozma.
- Kod faqat professional, optimal va clean-code darajasida yozilsin.
- Android kodida architecture qatlamlari aniq bo'lsin; UI, domain va data mas'uliyatlari aralashmasin.
- `ViewModel -> UiState -> Effect` oqimi saqlansin, one-off hodisalar state flag bilan emas effect orqali boshqarilsin.
- Constructor injection afzal, yashirin service locator va global access ishlatilmasin.
- `data class` va immutable state afzal bo'lsin.
- Build va verification faqat `./gradlew` yoki `just` orqali bajarilsin.
- O'zgargan kod uchun minimal gate: `just verify`.
- Task tugagach avtomatik self-review qilinsin; regression, performance va lifecycle risk tekshirilsin.

## Tezkor runtime verification

- Android smoke va takrorlanuvchi regressiya verification'ida `Maestro MCP` birinchi tanlov bo'lsin; MCP yo'q bo'lsa `maestro` CLI fallback bo'lsin.
- Targeted debug uchun mavjud boot bo'lgan emulator, `adb`, `logcat`, UI tree va kerak bo'lsa `test-android-apps:android-emulator-qa` ishlatilsin.
- Deep link, test data va minimal reproduksiya afzal bo'lsin; qayta onboarding default bo'lmasin.
- Screenshot diagnostika uchun emas, final evidence uchun olinsin; log va hierarchy asosiy signal bo'lsin.
