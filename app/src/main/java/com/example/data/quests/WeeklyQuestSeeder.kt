package com.example.data.quests

internal object WeeklyQuestSeeder {

    private fun sg(id: String, titleEn: String, titleAr: String, descEn: String, descAr: String, target: Int, xp: Long) =
        QuestEntity(id, titleEn, titleAr, descEn, descAr, QuestFrequency.WEEKLY, QuestCategory.STREAK_GUARDIANS, target, 0, xp)

    private fun mb(id: String, titleEn: String, titleAr: String, descEn: String, descAr: String, target: Int, xp: Long) =
        QuestEntity(id, titleEn, titleAr, descEn, descAr, QuestFrequency.WEEKLY, QuestCategory.MYSTERY_BOX_HUNTERS, target, 0, xp)

    private fun df(id: String, titleEn: String, titleAr: String, descEn: String, descAr: String, target: Int, xp: Long) =
        QuestEntity(id, titleEn, titleAr, descEn, descAr, QuestFrequency.WEEKLY, QuestCategory.DEEP_FASTING, target, 0, xp)

    private fun nu(id: String, titleEn: String, titleAr: String, descEn: String, descAr: String, target: Int, xp: Long) =
        QuestEntity(id, titleEn, titleAr, descEn, descAr, QuestFrequency.WEEKLY, QuestCategory.NEURAL_UPGRADES, target, 0, xp)

    val allWeeklyQuests: List<QuestEntity> = listOf(
        // --- 1. STREAK_GUARDIANS (WQ_001 - WQ_025) ---
        sg("WQ_001", "5-Day Flame Sentinel", "حارس شعلة 5 أيام", "Maintain an unbroken 5-day adherence streak.", "حافظ على سلسلة التزام متصلة لـ 5 أيام.", 5, 2200L),
        sg("WQ_002", "Full 7-Day Perfect Week", "أسبوع التتابع المثالي (7 أيام)", "Protect your Safety Buffer > 0 every single day for 7 days.", "احمِ مخزن الأمان فوق 0 لـ 7 أيام متتالية.", 7, 3500L),
        sg("WQ_003", "10-Day Unbreakable Legend", "أسطورة الـ 10 أيام", "Extend or hold a continuous streak of at least 10 days.", "صل إلى سلسلة متواصلة من 10 أيام على الأقل.", 10, 4200L),
        sg("WQ_004", "Zero-Demotion Iron Week", "أسبوع بلا أي هبوط", "Complete 7 days without suffering a single brain level drop.", "أنهِ 7 أيام دون هبوط واحد في مستوى الدماغ.", 7, 3000L),
        sg("WQ_005", "Triple Clean Sweep Week", "ثلاثية الفوز الناصع", "Achieve 3 spotless 1,000/1,000 buffer days this week.", "حقق 3 أيام فوز ناصع بمخزن 1,000/1,000 هذا الأسبوع.", 3, 2800L),
        sg("WQ_006", "5-Time Buffer Guardian", "حارس المخزن 5 مرات", "Survive 5 days this week with >= 800/1,000 buffer left.", "تجاوز 5 أيام هذا الأسبوع بـ >= 800 نقطة مخزن.", 5, 3200L),
        sg("WQ_007", "Weekend Discipline Fortress", "حصن انضباط عطلة نهاية الأسبوع", "Avoid streak resets across Saturday & Sunday.", "احمِ سلسلتك من الانكسار طوال عطلة الأسبوع.", 2, 2400L),
        sg("WQ_008", "Flame 90+ Consistency", "ثبات الشعلة فوق 90", "End 5 consecutive days with Flame Score >= 90.", "أنهِ 5 أيام متتالية بشعلة تساوي 90 أو أكثر.", 5, 2600L),
        sg("WQ_009", "Zero Shield Consumption", "صون الدروع الأسبوعي", "Go the entire week without burning a single Shield item.", "اعبر الأسبوع كاملاً دون استهلاك أي درع.", 7, 2900L),
        sg("WQ_010", "14-Day Vanguard", "طليعة الـ 14 يوماً", "Reach a 14-day cumulative discipline streak.", "بلغ سلسلة تتابع مستمرة لمدة 14 يوماً.", 14, 4800L),
        sg("WQ_011", "Quadruple Clean Sweeps", "رباعية الفوز الناصع", "Record 4 zero-breach Clean Sweep days within one week.", "سجل 4 أيام خالية تماماً من التجاوزات في أسبوع.", 4, 3400L),
        sg("WQ_012", "21-Day Habit Forged", "صياغة عادة 21 يوماً", "Reach the 21-day neural habit lock milestone.", "صل إلى محطة تثبيت العادة لـ 21 يوماً متواصلة.", 21, 5500L),
        sg("WQ_013", "30-Day Dragon Egg Ascension", "صعود بيضة التنين (30 يوماً)", "Hold a legendary 30-day unbroken streak.", "حافظ على سلسلة أسطورية متواصلة لـ 30 يوماً.", 30, 7500L),
        sg("WQ_014", "Zero Reset Recovery", "التصدي التام للتصفير", "Avoid any buffer exhaustion (0/1000) across 7 evaluations.", "تجنب استنفاد المخزن (0/1000) خلال 7 تقييمات.", 7, 3100L),
        sg("WQ_015", "Monday Anchor Lock", "مرساة انطلاقة الأسبوع", "Start Monday and Tuesday with back-to-back Clean Sweeps.", "ابدأ الإثنين والثلاثاء بفوزين ناصعين متتاليين.", 2, 2100L),
        sg("WQ_016", "Midweek Storm Survival", "اجتياز عاصفة منتصف الأسبوع", "Keep Safety Buffer >= 700 across Wednesday and Thursday.", "حافظ على مخزن >= 700 يومي الأربعاء والخميس.", 2, 2200L),
        sg("WQ_017", "5-Day Breachless Social", "5 أيام تواصل بلا تجاوز", "Spend 5 days with zero Social Media breaches.", "اكمل 5 أيام دون أي اختراق لتطبيقات التواصل.", 5, 3300L),
        sg("WQ_018", "4-Day Breachless Gaming", "4 أيام ألعاب بلا تجاوز", "Spend 4 days with zero Entertainment/Game breaches.", "اكمل 4 أيام دون أي اختراق في الألعاب والترفيه.", 4, 2700L),
        sg("WQ_019", "Milestone 5x Claimed", "استلام مكافأة الـ 5 أيام", "Trigger and claim at least 1 5-Day Streak Milestone drop.", "احصل على سحب مكافأة سلسلة كل 5 أيام مرة واحدة.", 1, 2000L),
        sg("WQ_020", "Double Clean Sweep Finish", "خاتمة فوز ناصع مزدوج", "Close the last 2 days of the week breach-free.", "أغلق آخر يومين من الأسبوع دون أي تجاوزات.", 2, 2300L),
        sg("WQ_021", "950-Pt Buffer Trio", "ثلاثي حصانة الـ 950", "Finish 3 days with >= 950 / 1,000 safety buffer.", "أنهِ 3 أيام وبالمخزن 950 نقطة أو أكثر.", 3, 2900L),
        sg("WQ_022", "Zero Late-Night Collapse", "صمود ساعات الليل المتأخرة", "Prevent any night breach across 6 days this week.", "امنع أي تجاوز ليلي لـ 6 أيام هذا الأسبوع.", 6, 2800L),
        sg("WQ_023", "5-Day Escalating Bonus Run", "5 مكافآت تتابع تصاعدية", "Collect escalating streak XP bonuses 5 times.", "احصد مكافآت التتابع التصاعدية 5 مرات.", 5, 3000L),
        sg("WQ_024", "Aegis Fortress Week", "أسبوع حصن الأيجيس", "Maintain active immunity or 0 demotions for 7 straight days.", "حافظ على حصانة تامة دون هبوط 7 أيام متصلة.", 7, 3600L),
        sg("WQ_025", "Clean Sweep Grand Week", "أسبوع الفوز الناصع العظيم", "Achieve 6 Clean Sweeps inside a single 7-day cycle.", "حقق 6 أيام فوز ناصع داخل أسبوع واحد.", 6, 5000L),

        // --- 2. MYSTERY_BOX_HUNTERS (WQ_026 - WQ_050) ---
        mb("WQ_026", "First Cyber Box Forged", "صياغة أول صندوق سايبر", "Craft or obtain 1 Mystery Cyber Box in the Lab.", "اصنع أو احصل على صندوق سايبر غامض واحد.", 1, 2200L),
        mb("WQ_027", "Vault Collector Novice", "جامع غنائم المبتدئ", "Open 2 Mystery Cyber Boxes to expand your arsenal.", "افتح صندوقي سايبر غامضين لتعزيز ترسانتك.", 2, 3200L),
        mb("WQ_028", "Key Fusion Alchemist", "خيميائي صهر المفاتيح", "Fuse 3 Daily Skip Keys into 1 Weekly Key in the Fusion Lab.", "ادمج 3 مفاتيح يومية في مفتاح أسبوعي واحد.", 1, 2600L),
        mb("WQ_029", "Aegis Shield Smith", "حداد درع الحصانة", "Forge a 24-Hour Shield inside the Neural Fusion Lab.", "اصنع درع حماية 24 ساعة في مختبر الدمج.", 1, 2400L),
        mb("WQ_030", "Potion Brewer Initiate", "صانع جرعة التعافي", "Synthesize 1 Brain Healing Potion from Weekly Keys.", "خلّق جرعة تعافي ذهني واحدة من المفاتيح الأسبوعية.", 1, 3000L),
        mb("WQ_031", "Daily Quest Harvester", "حصاد المهام اليومية", "Complete 10 Daily Quests throughout this week.", "أنجز 10 مهام يومية خلال هذا الأسبوع.", 10, 2800L),
        mb("WQ_032", "Daily Quest Conqueror", "فاتح المهام اليومية", "Complete 15 Daily Quests within a single week.", "أنجز 15 مهمة يومية خلال أسبوع واحد.", 15, 3800L),
        mb("WQ_033", "Silver Egg Seeker", "باحث البيضة الفضية", "Unlock or progress toward the 7-Day Silver Egg.", "احرز تقدماً أو افتح البيضة الفضية ذات 7 أيام.", 7, 2900L),
        mb("WQ_034", "Bronze Egg Breaker", "كاسر البيضة البرونزية", "Hatch or sustain eligibility for the Bronze Streak Egg.", "حقق استحقاق أو فقس البيضة البرونزية بنجاح.", 3, 2200L),
        mb("WQ_035", "XP Treasury Builder", "باني خزينة الخبرة", "Accumulate +5,000 total XP across quests & streaks this week.", "اجمع +5,000 نقطة خبرة هذا الأسبوع.", 5000, 3200L),
        mb("WQ_036", "XP Vault Expansion", "توسيع قبو الخبرة", "Accumulate +10,000 total XP in a single week.", "اجمع +10,000 نقطة خبرة خلال أسبوع واحد.", 10000, 4500L),
        mb("WQ_037", "Double Fusion Protocol", "بروتوكول الدمج المزدوج", "Perform 2 crafting or fusion recipes in the Inventory Lab.", "نفّذ عمليتي صهر أو صناعة في مختبر المستودع.", 2, 2800L),
        mb("WQ_038", "Tactical Skip Strategist", "خبير التخطي التكتيكي", "Use a Daily or Weekly Skip Key strategically on a tough quest.", "استخدم مفتاح تخطي بشكل استراتيجي هذا الأسبوع.", 1, 2100L),
        mb("WQ_039", "Triple Cyber Crate", "ثلاثية صناديق السايبر", "Open 3 Mystery Cyber Boxes to hunt rare Legendary drops.", "افتح 3 صناديق سايبر لصيد الجرعات النادرة.", 3, 4200L),
        mb("WQ_040", "Legendary Loot Aspirant", "صائد الغنائم الأسطورية", "Collect 4 total inventory items from drops, eggs, or crafting.", "اجمع 4 عناصر مخزون من السحوبات أو التصنيع.", 4, 3400L),
        mb("WQ_041", "Shield Stockpiler", "مخزّن دروع الحماية", "Hold 2 Shield items (24H or 1-Week) in your Vault simultaneously.", "احتفظ بدرعي حماية معاً في المستودع.", 2, 2700L),
        mb("WQ_042", "Potion Catalyst Lab", "مختبر تحفيز الجرعات", "Drink a Healing or Legendary Potion to ascend faster.", "استخدم جرعة تعافي أو أسطورية للارتقاء السريع.", 1, 2600L),
        mb("WQ_043", "Key Vault Hoarder", "جامع المفاتيح النادرة", "Accumulate 3 Skip Keys across Daily and Weekly tiers.", "اجمع 3 مفاتيح تخطي يومية أو أسبوعية.", 3, 2900L),
        mb("WQ_044", "Reward Milestone Hunter", "صائد محطات الجوائز", "Claim 2 milestone or egg rewards without missing a day.", "استلم جائزتي محطات أو بيض دون انقطاع.", 2, 3300L),
        mb("WQ_045", "Weekly Quest Master", "سيد المهام الأسبوعية", "Clear 3 Weekly Quests during the active 7-day cycle.", "أنجز 3 مهام أسبوعية خلال دورة الـ 7 أيام.", 3, 3500L),
        mb("WQ_046", "Weekly Codex Hunter", "صائد السجل الأسبوعي", "Complete 5 Weekly Quests inside this week.", "أنهِ 5 مهام أسبوعية هذا الأسبوع.", 5, 4800L),
        mb("WQ_047", "Mystery Box Synthesis Run", "سلسلة تصنيع الصناديق", "Earn enough XP to craft 2 Mystery Cyber Boxes in one week.", "اجمع خبرة تكفي لصنع صندوقي سايبر غامضين.", 2, 3600L),
        mb("WQ_048", "Arsenal Readiness 5x", "جاهزية الترسانة ×5", "Check and optimize your Vault & Fusion recipes 5 times.", "تفقّد وجهّز مخزونك ووصفات الدمج 5 مرات.", 5, 2200L),
        mb("WQ_049", "Dragon Egg Trailblazer", "رائد بيضة التنين", "Advance 7 unbroken days toward the Dragon Egg vault.", "تقدّم 7 أيام متصلة نحو بيضة التنين الأسطورية.", 7, 3900L),
        mb("WQ_050", "Grand Loot Hoarder", "جامع الغنائم الأعظم", "Finish 18 Daily Quests and open at least 1 Box in a week.", "أنجز 18 مهمة يومية وافتح صندوقاً واحداً على الأقل.", 18, 5200L),

        // --- 3. DEEP_FASTING (WQ_051 - WQ_075) ---
        df("WQ_051", "Total Digital Fast 3x", "الصيام الرقمي التام ×3", "Achieve 3 full days under 30 mins Social Media total.", "حقق 3 أيام بأقل من 30 دقيقة تواصل إجمالاً.", 3, 3200L),
        df("WQ_052", "Zero Shorts / Reels Week", "أسبوع خالٍ من المقاطع القصيرة", "Avoid short-form dopamine loops for 5 days this week.", "ابتعد عن الفيديوهات القصيرة 5 أيام هذا الأسبوع.", 5, 3600L),
        df("WQ_053", "Morning Fasting Master", "سيد صيام الصباح", "Stay off social apps every morning until noon for 5 days.", "امتنع عن التواصل كل صباح حتى الظهيرة 5 أيام.", 5, 3000L),
        df("WQ_054", "Digital Sunset Covenant", "عهد الغروب الرقمي", "Disconnect from entertainment feeds after 9 PM for 5 nights.", "انفصل عن الترفيه بعد 9 مساءً 5 ليالٍ.", 5, 3100L),
        df("WQ_055", "Brain Rot Zero Zone", "منطقة الصفر لتعفن الدماغ", "Keep weekly Brain Rot average strictly below 10%.", "حافظ على متوسط تعفن الدماغ دون 10% طوال الأسبوع.", 7, 3800L),
        df("WQ_056", "Monk-Mode Sabbath", "يوم العزلة الرقمية التامة", "Take 1 full day with under 15 mins total recreational screen time.", "خصص يوماً كاملاً بأقل من 15 دقيقة ترفيه رقمي.", 1, 3500L),
        df("WQ_057", "Gaming Fast 5 Days", "صيام الألعاب 5 أيام", "Keep Gaming & Entertainment apps at 0 mins for 5 days.", "سجل 0 دقيقة ألعاب لمدة 5 أيام هذا الأسبوع.", 5, 3400L),
        df("WQ_058", "Binge Detox Protocol", "بروتوكول تطهير المشاهدة", "Avoid multi-episode binge watching for 6 straight days.", "تجنب المشاهدة المتواصلة لـ 6 أيام متتالية.", 6, 3300L),
        df("WQ_059", "Deep Sleep Cortex Cleanse", "تطهير نوم القشرة", "Ban bedroom phone use before sleep for 5 nights.", "امنع استخدام الهاتف في السرير قبل النوم 5 ليالٍ.", 5, 2900L),
        df("WQ_060", "Social App Fast 48h", "صيام تواصل 48 ساعة", "Go 2 consecutive days with near-zero social feed usage.", "اقضِ يومين متتاليين بصفر تصفح اجتماعي.", 2, 3400L),
        df("WQ_061", "Quiet Commute Pact", "ميثاق التنقل الصامت", "Complete 5 commutes or walks screen-free this week.", "أكمل 5 تنقلات أو جولات مشي بلا شاشة.", 5, 2500L),
        df("WQ_062", "Zero Rage-Scroll Week", "أسبوع خالٍ من الغضب الرقمي", "Avoid viral drama and outrage feeds all 7 days.", "تجنب محتوى الجدل والاستفزاز طوال 7 أيام.", 7, 2900L),
        df("WQ_063", "Airplane Focus Days 4x", "أيام وضع الطيران ×4", "Run 4 dedicated offline Airplane Mode blocks this week.", "نفّذ 4 فترات عمل في وضع الطيران هذا الأسبوع.", 4, 2600L),
        df("WQ_064", "Offline Meal Ritual", "طقس الوجبات الهادئة", "Eat 14 meals without watching videos or feeds this week.", "تناول 14 وجبة بلا فيديوهات أو تصفح هذا الأسبوع.", 14, 2800L),
        df("WQ_065", "Notification Fast 5x", "صيام التنبيهات ×5", "Spend 5 separate work evenings completely on Do Not Disturb.", "فعّل وضع عدم الإزعاج 5 أمسيات عمل هذا الأسبوع.", 5, 2700L),
        df("WQ_066", "Grayscale Week Discipline", "أسبوع الشاشة الرمادية", "Use grayscale display mode during focus blocks 4 days.", "استخدم التدرج الرمادي أثناء التركيز 4 أيام.", 4, 2600L),
        df("WQ_067", "Paper Book Revival", "إحياء القراءة الورقية", "Read 100 cumulative minutes from offline books or articles.", "اقرأ 100 دقيقة من الكتب أو الأوراق هذا الأسبوع.", 100, 3200L),
        df("WQ_068", "Social Budget Halved", "خفض ميزانية التواصل للنصف", "Finish 4 days consuming under 50% of your dynamic limit.", "أنهِ 4 أيام بأقل من نصف الحد الزمني المسموح.", 4, 3400L),
        df("WQ_069", "No-Impulse Shopping Week", "أسبوع صيام التسوق", "Keep shopping & browse apps unused for 6 days.", "تجنب تطبيقات التسوق والتصفح العشوائي 6 أيام.", 6, 2700L),
        df("WQ_070", "Mindful Pause Mastery", "إتقان التوقف الواعي", "Practice 5-second pause before opening Boss Apps 20 times.", "توقف 5 ثوانٍ بوعي قبل فتح المشتتات 20 مرة.", 20, 2600L),
        df("WQ_071", "Dopamine Baseline Reset", "إعادة ضبط خط الدوبامين", "Complete 5 Clean Sweep Dopamine Fasting days in 7 days.", "أتمّ 5 أيام صيام دوبامين ونظافة رقمية في أسبوع.", 5, 4200L),
        df("WQ_072", "Zero Autoplay Week", "أسبوع رفض التشغيل التلقائي", "Keep autoplay disabled and exit feeds on time 10 times.", "عطل التشغيل التلقائي وأغلق التطبيقات بوعي 10 مرات.", 10, 2500L),
        df("WQ_073", "Evening Silence Trio", "ثلاثي الصمت المسائي", "End 3 evenings completely offline after 8 PM.", "أغلق كل الشاشات تماماً بعد 8 مساءً لـ 3 أمسيات.", 3, 3000L),
        df("WQ_074", "Addiction Purge Overdrive", "التطهير الفائق للإدمان", "Drop Brain Rot to 0.0% and hold it there for 3 days.", "اخفض تعفن الدماغ إلى 0.0% وثبّته 3 أيام.", 3, 3900L),
        df("WQ_075", "7-Day Ultra Monk Fast", "الصيام الأسطوري 7 أيام", "Keep Boss Apps under half limit across every day of the week.", "أبقِ التطبيقات المشتتة دون نصف حدها طوال الـ 7 أيام.", 7, 5500L),

        // --- 4. NEURAL_UPGRADES (WQ_076 - WQ_100) ---
        nu("WQ_076", "Level Ascension +1", "ارتقاء عصبي +1 مستوى", "Promote your Brain Level by at least +1 level this week.", "ارفع مستوى دماغك +1 مستوى على الأقل هذا الأسبوع.", 1, 2400L),
        nu("WQ_077", "Dual Brain Evolution +2", "تطور دماغي مزدوج +2", "Ascend +2 Brain Levels through XP mastery.", "ارتقِ مستويين كاملين عبر كسب نقاط الخبرة.", 2, 3600L),
        nu("WQ_078", "Quantum Surge +3 Levels", "قفزة كمومية +3 مستويات", "Climb +3 full Brain Levels within 7 days.", "اصعد 3 مستويات كاملة خلال 7 أيام.", 3, 5000L),
        nu("WQ_079", "Weekly Focus Titan (120m)", "عملاق التركيز الأسبوعي (120 د)", "Accumulate 120 minutes in Productivity & Learning tools.", "اجمع 120 دقيقة في تطبيقات الإنتاجية والتعلم.", 120, 3000L),
        nu("WQ_080", "Deep Work Marathon (240m)", "ماراثون العمل العميق (240 د)", "Log 240 minutes in high-focus productive apps this week.", "سجل 240 دقيقة تركيز عميق منتج هذا الأسبوع.", 240, 4200L),
        nu("WQ_081", "Neural Singularity (360m)", "تفرد عصبي (360 دقيقة)", "Accumulate 6 full hours (360 mins) of productive deep work.", "أكمل 6 ساعات كاملة (360 دقيقة) عمل مركز.", 360, 5600L),
        nu("WQ_082", "Kinetic Body Temple 4x", "معبد الطاقة الحركية ×4", "Complete 4 physical workouts or 25m cardio sessions.", "أكمل 4 حصص رياضية أو جولات مشي نشط.", 4, 3000L),
        nu("WQ_083", "Hydration Mastery Week", "إتقان الترطيب الأسبوعي", "Hit daily hydration goals on 6 out of 7 days.", "حقق هدف شرب الماء في 6 من أصل 7 أيام.", 6, 2500L),
        nu("WQ_084", "Sleep Architecture Upgrade", "ترقية هندسة النوم", "Sleep on schedule with zero screens before bed 5 nights.", "نم في موعدك بلا شاشات لـ 5 ليالٍ متتالية.", 5, 3100L),
        nu("WQ_085", "Morning Victory Chain", "سلسلة انتصارات الصباح", "Start 5 mornings with exercise or learning before feeds.", "ابدأ 5 صباحات بالرياضة أو التعلم قبل التصفح.", 5, 3400L),
        nu("WQ_086", "Knowledge Artifact Maker", "صانع مخرجات المعرفة", "Complete 4 written summaries, study sets, or work deliverables.", "أنجز 4 ملخصات أو مشاريع عملية هذا الأسبوع.", 4, 3200L),
        nu("WQ_087", "Skill Rank Advancement", "التقدم نحو الرتبة التالية", "Add at least +2,500 XP toward your next Neural Rank.", "أضف +2,500 XP نحو رتبتك العصبية القادمة.", 2500, 3000L),
        nu("WQ_088", "Rank Tier Breakthrough", "اختراق الرتبة العصبية", "Cross a 5-level milestone or enter a stronger shrink tier.", "تجاوز محطة 5 مستويات أو تحدَّ تقليص الحدود بنجاح.", 1, 3500L),
        nu("WQ_089", "Pomodoro Centurion", "قائد المائة بومودورو", "Log 8 dedicated 25-min deep work sprints this week.", "نفّذ 8 جلسات بومودورو مركزة ×25 دقيقة.", 8, 3400L),
        nu("WQ_090", "Clarity & Detox Balance", "توازن الصفاء والتعافي", "Heal Brain Rot cleanly below 5% across 5 evaluations.", "حافظ على تعفن الدماغ دون 5% خلال 5 تقييمات.", 5, 3300L),
        nu("WQ_091", "10,000-Step Stride Trio", "ثلاثي العشرة آلاف خطوة", "Hit 10,000 kinetic steps on 3 days this week.", "بلغ 10,000 خطوة حركة في 3 أيام هذا الأسبوع.", 3, 3200L),
        nu("WQ_092", "Shrinking Time Conqueror", "قاهر تقلص الوقت", "Succeed with 0 breaches under newly tightened level caps.", "انجح بـ 0 تجاوزات تحت الحدود الزمنية المقلصة.", 3, 3800L),
        nu("WQ_093", "Skill Practice Streak", "تتابع صقل المهارة", "Practice coding, language, or deep craft 5 days in a row.", "مارس تعلم مهارة أساسية 5 أيام متتابعة.", 5, 3500L),
        nu("WQ_094", "No-Snooze Dawn Warrior", "محارب الفجر بلا غفوة", "Wake on first alarm for 5 straight mornings.", "استيقظ من المنبه الأول 5 صباحات متتالية.", 5, 2900L),
        nu("WQ_095", "Sunlight & Oxygen Sync", "مزامنة الشمس والأكسجين", "Get 15 mins morning sunlight + walk 5 days this week.", "احصل على شمس الصباح + مشي 5 أيام هذا الأسبوع.", 5, 2800L),
        nu("WQ_096", "Focus Over Dopamine Ratio", "غلبة التركيز على الدوبامين", "Ensure weekly Productivity time exceeds Social Media time.", "اجعل وقت الإنتاجية الأسبوعي يفوق وقت التواصل.", 1, 4000L),
        nu("WQ_097", "Digital Minimalist Audit", "تدقيق التبسيط الرقمي", "Purge clutter & organize workspace/device 3 times.", "نظّف جهازك ومكتبك من الفوضى 3 مرات.", 3, 2400L),
        nu("WQ_098", "Bilingual Focus Polymath", "توسيع الأفق المعرفي", "Invest 150 total minutes into reading & skill mastery.", "استثمر 150 دقيقة في القراءة وإتقان المهارات.", 150, 3600L),
        nu("WQ_099", "Ascended Focus Week (480m)", "أسبوع التركيز السامي (480 د)", "Reach 8 hours (480 mins) of tracked productive effort.", "حقق 8 ساعات (480 دقيقة) من العمل المثمر المرصود.", 480, 6200L),
        nu("WQ_100", "The Singularity Neural Week", "الأسبوع العصبي المتفرد", "Combine +2 Levels, 5+ Streak Days & 180m Deep Focus.", "اجمع +2 مستوى + 5 أيام تتابع + 180 د تركيز عميق.", 1, 7500L)
    )
}
