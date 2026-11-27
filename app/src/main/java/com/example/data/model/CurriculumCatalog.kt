package com.example.data.model

object CurriculumCatalog {

    val boards = listOf("CBSE", "ICSE", "State Board", "Other")
    val classLevels = listOf(
        "Class 1", "Class 2", "Class 3", "Class 4", "Class 5",
        "Class 6", "Class 7", "Class 8",
        "Class 9", "Class 10", "Class 11", "Class 12"
    )
    val categories = listOf("All", "Class 1-5", "Class 6-8", "Class 9-10", "Class 11-12")
    val mediums = listOf(
        "English", "Hindi", "Tamil", "Telugu", "Bengali", "Marathi",
        "Gujarati", "Kannada", "Malayalam", "Punjabi", "Odia",
        "Urdu", "Assamese", "Sanskrit", "Maithili", "Bilingual"
    )
    val allSubjects = listOf("Mathematics", "Science", "English", "Hindi", "Social Science")

    val subscriptionPlans: List<SubscriptionPlan> = listOf(
        SubscriptionPlan(
            tierId = "FREE",
            name = "Free Scholar",
            priceInr = 0,
            durationLabel = "Forever Free",
            badge = null,
            features = listOf(
                "Core NCERT & Board digital textbooks",
                "Basic chapter notes & daily practice quiz",
                "5 AI Teacher questions per day",
                "Personal study planner & streak tracker"
            )
        ),
        SubscriptionPlan(
            tierId = "MONTHLY",
            name = "Monthly Plus",
            priceInr = 99,
            durationLabel = "1 Month",
            badge = "Flexible",
            features = listOf(
                "Complete Class 1–12 digital textbook library",
                "Unlimited AI Teacher & Explain Paragraph",
                "Premium revision notes, PYQs & formula sheets",
                "Full mock exams & detailed weak-topic reports"
            )
        ),
        SubscriptionPlan(
            tierId = "QUARTERLY",
            name = "Quarterly Pro",
            priceInr = 249,
            durationLabel = "3 Months",
            badge = "Save 16%",
            features = listOf(
                "All Monthly Plus benefits for 90 days",
                "Offline textbook chapter caching",
                "Video lecture library with speed controls",
                "Priority doubt-solving & practice generators"
            )
        ),
        SubscriptionPlan(
            tierId = "HALF_YEARLY",
            name = "Half-Yearly Ace",
            priceInr = 449,
            durationLabel = "6 Months",
            badge = "Exam Ready",
            features = listOf(
                "Complete term & pre-board exam series",
                "Unlimited AI Teacher in English & Hindi",
                "10-Year Previous Year Papers with solutions",
                "Advanced weekly & monthly performance analytics"
            )
        ),
        SubscriptionPlan(
            tierId = "YEARLY",
            name = "Yearly Topper",
            priceInr = 799,
            durationLabel = "12 Months",
            badge = "Most Popular",
            features = listOf(
                "Full academic session access (Save 33%)",
                "All Boards: CBSE, ICSE & State Boards",
                "Unlimited AI Teacher, Mock Exams & Video Lessons",
                "Complete offline library & handwritten revision sheets"
            )
        )
    )

    val starterTextbooks: List<Textbook> = listOf(
        Textbook(
            id = "book_math_10",
            title = "Mathematics — Class 10",
            subject = "Mathematics",
            classLevel = "Class 10",
            category = "Class 9-10",
            board = "CBSE",
            medium = "English",
            academicYear = "2026-27",
            description = "Comprehensive board-aligned textbook covering Real Numbers, Polynomials, Quadratic Equations, Trigonometry, Coordinate Geometry, and Statistics with solved proofs.",
            chapterCount = 3,
            premiumOnly = false,
            published = true,
            accentHex = "#183B56"
        ),
        Textbook(
            id = "book_sci_10",
            title = "Science — Class 10",
            subject = "Science",
            classLevel = "Class 10",
            category = "Class 9-10",
            board = "CBSE",
            medium = "English",
            academicYear = "2026-27",
            description = "Integrated Physics, Chemistry, and Biology textbook featuring Chemical Reactions, Life Processes, Light Reflection & Refraction, and Electricity.",
            chapterCount = 3,
            premiumOnly = false,
            published = true,
            accentHex = "#0D9488"
        ),
        Textbook(
            id = "book_eng_10",
            title = "First Flight — English",
            subject = "English",
            classLevel = "Class 10",
            category = "Class 9-10",
            board = "CBSE",
            medium = "English",
            academicYear = "2026-27",
            description = "Core prose, poetry, grammar mastery, and analytical writing designed to build literary appreciation and board-exam precision.",
            chapterCount = 2,
            premiumOnly = false,
            published = true,
            accentHex = "#4F46E5"
        ),
        Textbook(
            id = "book_hin_10",
            title = "क्षितिज भाग-2 (Hindi)",
            subject = "Hindi",
            classLevel = "Class 10",
            category = "Class 9-10",
            board = "CBSE",
            medium = "Hindi",
            academicYear = "2026-27",
            description = "कक्षा 10 के लिए काव्य खंड, गद्य खंड, और व्याकरण का संपूर्ण डिजिटल पाठ्यक्रम सरल व्याख्या के साथ।",
            chapterCount = 2,
            premiumOnly = false,
            published = true,
            accentHex = "#D97706"
        ),
        Textbook(
            id = "book_sst_10",
            title = "India & Contemporary World",
            subject = "Social Science",
            classLevel = "Class 10",
            category = "Class 9-10",
            board = "CBSE",
            medium = "Bilingual",
            academicYear = "2026-27",
            description = "Modern history, democratic politics, Indian geography, and economics structured with timelines, map skills, and source analysis.",
            chapterCount = 2,
            premiumOnly = true,
            published = true,
            accentHex = "#BE123C"
        ),
        Textbook(
            id = "book_sci_8",
            title = "Living Science — Class 8",
            subject = "Science",
            classLevel = "Class 8",
            category = "Class 6-8",
            board = "ICSE",
            medium = "English",
            academicYear = "2026-27",
            description = "Middle-school conceptual science exploring Force & Pressure, Microorganisms, Cell Structure, and Conservation of Nature.",
            chapterCount = 2,
            premiumOnly = false,
            published = true,
            accentHex = "#0D9488"
        ),
        Textbook(
            id = "book_math_5",
            title = "Math-Magic — Class 5",
            subject = "Mathematics",
            classLevel = "Class 5",
            category = "Class 1-5",
            board = "CBSE",
            medium = "English",
            academicYear = "2026-27",
            description = "Foundational primary mathematics building visual intuition for Shapes, Angles, Fractions, Area, and Everyday Mental Arithmetic.",
            chapterCount = 2,
            premiumOnly = false,
            published = true,
            accentHex = "#183B56"
        ),
        Textbook(
            id = "book_phy_12",
            title = "Physics Part I — Class 12",
            subject = "Science",
            classLevel = "Class 12",
            category = "Class 11-12",
            board = "CBSE",
            medium = "English",
            academicYear = "2026-27",
            description = "Senior secondary physics covering Electrostatics, Gauss's Law, Current Electricity, and Electromagnetic Induction with derivations.",
            chapterCount = 2,
            premiumOnly = true,
            published = true,
            accentHex = "#0284C7"
        )
    )

    val starterChaptersByBook: Map<String, List<Chapter>> = mapOf(
        "book_math_10" to listOf(
            Chapter(
                id = "math10_ch1",
                bookId = "book_math_10",
                chapterNumber = 1,
                title = "Real Numbers",
                summary = "Explore the Fundamental Theorem of Arithmetic, prime factorization, HCF-LCM relations, and proofs of irrationality.",
                estimatedMinutes = 20,
                keyPoints = listOf(
                    "Fundamental Theorem of Arithmetic: Every composite number can be expressed uniquely as a product of primes, apart from order.",
                    "For any two positive integers a and b: HCF(a, b) × LCM(a, b) = a × b.",
                    "If a prime p divides a², then p divides a, where a is a positive integer.",
                    "√2, √3, and √5 are irrational numbers proved via proof by contradiction."
                ),
                paragraphs = listOf(
                    "Real numbers form the foundation of all school algebra and calculus. Every number that can be plotted on a continuous number line—whether rational like 3/4 and -7, or irrational like √2 and π—belongs to the set of real numbers. In this chapter, we examine two fundamental properties of positive integers: unique prime factorization and the nature of irrationality.",
                    "The Fundamental Theorem of Arithmetic states that every composite number can be factorized as a product of primes, and this factorization is unique apart from the order in which the prime factors occur. For example, 140 = 2 × 2 × 5 × 7 = 2² × 5 × 7. This theorem allows us to compute both the Highest Common Factor (HCF) and Least Common Multiple (LCM) cleanly.",
                    "To find the HCF of two positive integers using prime factorization, take the product of the smallest power of each common prime factor involved in the numbers. To find the LCM, take the product of the greatest power of each prime factor involved in the numbers. For example, for 6 = 2 × 3 and 20 = 2² × 5, HCF(6, 20) = 2 and LCM(6, 20) = 2² × 3 × 5 = 60. Notice that 2 × 60 = 120 = 6 × 20.",
                    "To prove that √2 is irrational, we use Proof by Contradiction. Assume to the contrary that √2 is rational, so √2 = a/b where a and b are co-prime integers and b ≠ 0. Squaring both sides gives 2b² = a², meaning 2 divides a² and therefore 2 divides a. Writing a = 2c gives 2b² = 4c², so b² = 2c², meaning 2 also divides b. This contradicts our assumption that a and b are co-prime; hence √2 is irrational."
                )
            ),
            Chapter(
                id = "math10_ch2",
                bookId = "book_math_10",
                chapterNumber = 2,
                title = "Introduction to Trigonometry",
                summary = "Understand trigonometric ratios of acute angles in right triangles, standard angle values, and fundamental identities.",
                estimatedMinutes = 25,
                keyPoints = listOf(
                    "sin θ = Opposite / Hypotenuse, cos θ = Adjacent / Hypotenuse, tan θ = Opposite / Adjacent.",
                    "Pythagorean Identity: sin²θ + cos²θ = 1 for every angle θ.",
                    "1 + tan²θ = sec²θ and 1 + cot²θ = cosec²θ.",
                    "Standard values: sin 30° = 1/2, cos 60° = 1/2, tan 45° = 1."
                ),
                paragraphs = listOf(
                    "Trigonometry is the study of relationships between the sides and angles of a triangle. Ancient astronomers used trigonometric ratios to measure distances to stars and planets, while modern engineers rely on them for navigation, architecture, and signal processing.",
                    "In a right-angled triangle ABC right-angled at B, let θ be the acute angle at A. The side opposite to angle A is BC (perpendicular), the side adjacent to angle A is AB (base), and the longest side opposite the right angle is AC (hypotenuse). The six trigonometric ratios are defined as sin A = BC/AC, cos A = AB/AC, tan A = BC/AB, along with their reciprocals cosec A, sec A, and cot A.",
                    "Applying Pythagoras' Theorem AB² + BC² = AC² and dividing every term by AC² yields (AB/AC)² + (BC/AC)² = 1, which gives the first fundamental trigonometric identity: cos²A + sin²A = 1. Dividing instead by AB² gives 1 + tan²A = sec²A, and dividing by BC² gives cot²A + 1 = cosec²A.",
                    "Worked Example: If sin A = 3/5 in a right triangle, let opposite = 3k and hypotenuse = 5k. By Pythagoras' Theorem, adjacent = √((5k)² - (3k)²) = 4k. Therefore, cos A = 4/5 and tan A = 3/4. Checking the identity: (3/5)² + (4/5)² = 9/25 + 16/25 = 25/25 = 1."
                )
            ),
            Chapter(
                id = "math10_ch3",
                bookId = "book_math_10",
                chapterNumber = 3,
                title = "Quadratic Equations",
                summary = "Solve ax² + bx + c = 0 by factorization, quadratic formula, and analyze the nature of roots using the discriminant.",
                estimatedMinutes = 22,
                keyPoints = listOf(
                    "Standard form: ax² + bx + c = 0 where a ≠ 0.",
                    "Quadratic Formula: x = (-b ± √(b² - 4ac)) / 2a.",
                    "Discriminant D = b² - 4ac determines the nature of roots.",
                    "D > 0: two distinct real roots; D = 0: two equal real roots; D < 0: no real roots."
                ),
                paragraphs = listOf(
                    "A quadratic equation in the variable x is an equation of the form ax² + bx + c = 0, where a, b, and c are real numbers and a ≠ 0. Many practical situations—such as calculating the dimensions of a rectangular garden with fixed area or finding the flight time of a thrown ball—naturally lead to quadratic equations.",
                    "The quadratic formula provides a universal method to find the roots of any quadratic equation: x = (-b ± √(b² - 4ac)) / 2a. The expression D = b² - 4ac inside the square root is called the Discriminant because it discriminates between three possible cases for the roots.",
                    "When D > 0, the equation has two distinct real roots. When D = 0, both roots coincide at x = -b / 2a. When D < 0, the square root of a negative number is not real, so the quadratic equation has no real roots. Always compute D first before attempting factorization!"
                )
            )
        ),
        "book_sci_10" to listOf(
            Chapter(
                id = "sci10_ch1",
                bookId = "book_sci_10",
                chapterNumber = 1,
                title = "Chemical Reactions & Equations",
                summary = "Learn how to balance chemical equations and classify combination, decomposition, displacement, and redox reactions.",
                estimatedMinutes = 20,
                keyPoints = listOf(
                    "Law of Conservation of Mass: Total atoms of each element must be equal on both sides.",
                    "Combination: A + B → AB (e.g. CaO + H₂O → Ca(OH)₂ + Heat).",
                    "Oxidation is gain of oxygen or loss of hydrogen; Reduction is loss of oxygen or gain of hydrogen.",
                    "Corrosion and rancidity are everyday oxidation effects."
                ),
                paragraphs = listOf(
                    "Whenever a chemical change occurs, we say that a chemical reaction has taken place. Observable signs include a change in state, a change in colour, evolution of a gas, or a change in temperature. Burning a clean magnesium ribbon in air produces dazzling white light and leaves behind white ash of magnesium oxide: 2Mg + O₂ → 2MgO.",
                    "According to the Law of Conservation of Mass, mass can neither be created nor destroyed in a chemical reaction. Therefore, the total number of atoms of each element on the reactant side must equal the number of atoms on the product side. Balancing equations ensures our chemical notation obeys this physical law.",
                    "Chemical reactions are grouped into four major types: Combination (two or more reactants form a single product), Decomposition (a single reactant breaks down via heat, light, or electricity), Displacement (a more reactive element displaces a less reactive one from its salt solution), and Double Displacement (exchange of ions between reactants).",
                    "In a Redox reaction, one substance gets oxidized while another gets reduced simultaneously. For example, when hydrogen gas passes over heated copper(II) oxide (CuO + H₂ → Cu + H₂O), CuO loses oxygen and is reduced to copper, while H₂ gains oxygen and is oxidized to water."
                )
            ),
            Chapter(
                id = "sci10_ch2",
                bookId = "book_sci_10",
                chapterNumber = 2,
                title = "Life Processes",
                summary = "Discover how living organisms carry out nutrition, respiration, internal transportation, and excretion.",
                estimatedMinutes = 24,
                keyPoints = listOf(
                    "Photosynthesis: 6CO₂ + 12H₂O → C₆H₁₂O₆ + 6O₂ + 6H₂O (in sunlight & chlorophyll).",
                    "Aerobic respiration occurs in mitochondria and yields 38 ATP per glucose molecule.",
                    "Human heart has four chambers preventing mixing of oxygenated and deoxygenated blood.",
                    "Nephron is the structural and functional filtration unit of the kidney."
                ),
                paragraphs = listOf(
                    "Maintenance processes in living organisms must continue even when we are asleep or sitting still. The vital processes that together perform this maintenance job—nutrition, respiration, transportation, and excretion—are known as Life Processes.",
                    "Autotrophs such as green plants synthesize their own food via photosynthesis. Chlorophyll absorbs light energy, splits water molecules into hydrogen and oxygen, and reduces carbon dioxide into carbohydrates. Stomata on leaf surfaces regulate gaseous exchange and transpiration.",
                    "Cellular respiration breaks down glucose in the cytoplasm into a three-carbon molecule called pyruvate. In the presence of oxygen inside mitochondria, pyruvate is completely broken down into carbon dioxide, water, and a large burst of ATP energy. In muscle cells during intense exercise, lack of oxygen converts pyruvate into lactic acid, causing cramps."
                )
            ),
            Chapter(
                id = "sci10_ch3",
                bookId = "book_sci_10",
                chapterNumber = 3,
                title = "Electricity",
                summary = "Master electric current, potential difference, Ohm's Law, series and parallel resistors, and Joule's Law of Heating.",
                estimatedMinutes = 25,
                keyPoints = listOf(
                    "Electric Current I = Q / t (SI unit: Ampere, A).",
                    "Ohm's Law: V = I × R at constant temperature (Resistance R in Ohms Ω).",
                    "Series: R_s = R₁ + R₂ + R₃; Parallel: 1/R_p = 1/R₁ + 1/R₂ + 1/R₃.",
                    "Joule's Law of Heating: H = I²Rt; Electric Power P = VI = I²R = V²/R."
                ),
                paragraphs = listOf(
                    "Electric current is expressed as the amount of electric charge flowing through a particular cross-sectional area in unit time: I = Q / t. By convention, the direction of electric current is taken as opposite to the direction of flow of electrons. The SI unit of charge is the Coulomb (C), and 1 Ampere equals 1 Coulomb per second.",
                    "German physicist Georg Simon Ohm discovered that the potential difference V across the ends of a metallic wire in an electric circuit is directly proportional to the current I flowing through it, provided its temperature remains the same: V = I × R, where R is resistance.",
                    "Domestic circuits connect appliances in parallel rather than series so that each appliance receives the full supply voltage (220 V), operates independently with its own switch, and reduces overall circuit resistance. Electric fuses rely on Joule's heating effect (H = I²Rt) to melt and break the circuit safely during overload."
                )
            )
        ),
        "book_eng_10" to listOf(
            Chapter(
                id = "eng10_ch1",
                bookId = "book_eng_10",
                chapterNumber = 1,
                title = "A Letter to God",
                summary = "Analyze G.L. Fuentes's poignant story exploring unwavering faith, human empathy, and situational irony.",
                estimatedMinutes = 16,
                keyPoints = listOf(
                    "Theme: Absolute faith versus human kindness and unintended irony.",
                    "Lencho's cornfield is destroyed by a sudden hailstorm just before harvest.",
                    "The postmaster secretly collects 70 pesos to preserve Lencho's faith.",
                    "Irony: Lencho calls the very postal workers who helped him 'a bunch of crooks'."
                ),
                paragraphs = listOf(
                    "Perched on the crest of a low hill, Lencho's house overlooked the entire valley of ripe corn dotted with flowers that always promised a good harvest. The only thing the earth needed was a gentle downpour. When heavy raindrops began to fall, Lencho joyfully compared them to new coins falling from the sky.",
                    "Suddenly, a strong wind began to blow and large hailstones pounded the valley for an hour until the field looked white as if covered with salt. Not a single leaf remained on the trees. Yet in the hearts of all who lived in that solitary house, there remained a single hope: help from God.",
                    "Lencho wrote a letter asking God for a hundred pesos to sow his field again. Moved by this extraordinary faith, the postmaster donated part of his salary and asked friends to contribute, gathering seventy pesos. When Lencho counted the money, he grew angry—not doubting God for a second, he assumed the post office staff had stolen the remaining thirty pesos, creating one of literature's classic examples of situational irony."
                )
            ),
            Chapter(
                id = "eng10_ch2",
                bookId = "book_eng_10",
                chapterNumber = 2,
                title = "Nelson Mandela: Long Walk to Freedom",
                summary = "Examine Mandela's historic inauguration address on courage, twin obligations, and the triumph of equality over apartheid.",
                estimatedMinutes = 18,
                keyPoints = listOf(
                    "10 May 1994 marked South Africa's first democratic, non-racial government.",
                    "Courage is not the absence of fear, but the triumph over it.",
                    "Twin obligations: duty to one's family and duty to one's community and country.",
                    "Both the oppressor and the oppressed are robbed of their humanity."
                ),
                paragraphs = listOf(
                    "On the bright autumn morning of 10 May 1994, dignitaries from more than 140 nations gathered at the Union Buildings amphitheatre in Pretoria to witness the installation of South Africa's first democratic, non-racial government. After three centuries of racial domination, a new era of reconciliation had begun.",
                    "Mandela reflects deeply on what courage truly means. Watching comrades stand firm against brutality taught him that courage is not the absence of fear, but the triumph over it. The brave person is not one who never feels afraid, but one who conquers that fear.",
                    "He writes that no one is born hating another person because of the colour of their skin or religion. People must learn to hate, and if they can learn to hate, they can be taught to love, for love comes more naturally to the human heart than its opposite."
                )
            )
        ),
        "book_hin_10" to listOf(
            Chapter(
                id = "hin10_ch1",
                bookId = "book_hin_10",
                chapterNumber = 1,
                title = "सूरदास के पद (Surdas ke Pad)",
                summary = "भ्रमरगीत से लिए गए सूरदास के चार पदों का भावार्थ, गोपियों का व्यंग्य और अनन्य प्रेम।",
                estimatedMinutes = 18,
                keyPoints = listOf(
                    "उद्धव के निर्गुण ब्रह्म के उपदेश पर गोपियों की वाक्पटुता और व्यंग्य।",
                    "कमल के पत्ते और तेल की गागर के उदाहरण द्वारा उद्धव की स्थिति का वर्णन।",
                    "ब्रजभाषा की मधुरता, उपमा और रूपक अलंकार का सुंदर प्रयोग।"
                ),
                paragraphs = listOf(
                    "सूरदास के 'भ्रमरगीत' से लिए गए इन पदों में श्रीकृष्ण के मथुरा चले जाने के बाद गोपियों की विरह-वेदना का मार्मिक चित्रण है। श्रीकृष्ण ने उद्धव के माध्यम से गोपियों को योग और निर्गुण ब्रह्म का संदेश भेजा, जिसे सुनकर गोपियाँ व्यंग्यपूर्वक उद्धव से संवाद करती हैं।",
                    "गोपियाँ उद्धव को 'बड़भागी' (अत्यंत भाग्यशाली) कहकर व्यंग्य करती हैं कि वे श्रीकृष्ण के पास रहकर भी उनके प्रेम के बंधन से वैसे ही अछूते हैं, जैसे जल के भीतर रहने वाला कमल का पत्ता पानी की बूँद से भीगता नहीं है और तेल लगी मटकी पर जल की बूँद ठहरती नहीं है।",
                    "इन पदों में गोपियों का श्रीकृष्ण के प्रति एकनिष्ठ प्रेम प्रकट होता है। वे स्वयं को उस चींटी के समान मानती हैं जो गुड़ से चिपक जाती है। यहाँ सूरदास ने सगुण भक्ति की सहजता को निर्गुण योग की कठोरता से अधिक प्रभावशाली दर्शाया है।"
                )
            ),
            Chapter(
                id = "hin10_ch2",
                bookId = "book_hin_10",
                chapterNumber = 2,
                title = "नेताजी का चश्मा (Netaji ka Chashma)",
                summary = "स्वयं प्रकाश की कहानी जो सामान्य नागरिकों के देशप्रेम और कैप्टन चश्मेवाले की संवेदनशीलता को दर्शाती है।",
                estimatedMinutes = 15,
                keyPoints = listOf(
                    "देशभक्ति केवल सीमा पर लड़ने तक सीमित नहीं, छोटे-छोटे कार्यों में भी झलकती है।",
                    "कैप्टन चश्मेवाला नेताजी सुभाषचंद्र बोस की मूर्ति पर नया चश्मा लगाता रहता था।",
                    "अंत में बच्चों द्वारा सरकंडे का चश्मा लगाना नई पीढ़ी में देशप्रेम का प्रतीक है।"
                ),
                paragraphs = listOf(
                    "हालदार साहब हर पंद्रहवें दिन कंपनी के काम से एक छोटे से कस्बे से गुजरते थे। कस्बे के मुख्य चौराहे पर नेताजी सुभाषचंद्र बोस की संगमरमर की मूर्ति लगी थी, लेकिन मूर्तिकार चश्मा बनाना भूल गया था। फिर भी मूर्ति पर हमेशा एक असली चश्मा लगा रहता था।",
                    "पानवाले से पूछने पर पता चला कि एक बूढ़ा, लँगड़ा देशभक्त जिसे लोग 'कैप्टन' कहते हैं, अपनी फेरी के चश्मों में से एक चश्मा नेताजी की मूर्ति को पहना देता था। यह कहानी सिखाती है कि देश केवल भूभाग से नहीं, बल्कि उसमें रहने वाले करोड़ों नागरिकों के प्रेम और योगदान से बनता है।"
                )
            )
        ),
        "book_sst_10" to listOf(
            Chapter(
                id = "sst10_ch1",
                bookId = "book_sst_10",
                chapterNumber = 1,
                title = "The Rise of Nationalism in Europe",
                summary = "Trace how the French Revolution, Romanticism, and the unifications of Italy and Germany shaped modern nation-states.",
                estimatedMinutes = 22,
                keyPoints = listOf(
                    "1789 French Revolution transferred sovereignty from monarchy to citizens.",
                    "Napoleonic Code of 1804 abolished feudal privileges and established equality before law.",
                    "Otto von Bismarck led German unification (1871); Cavour, Mazzini & Garibaldi unified Italy."
                ),
                paragraphs = listOf(
                    "During the nineteenth century, nationalism emerged as a transformative force that reshaped the political and mental landscape of Europe. Multi-national dynastic empires gradually gave way to nation-states in which citizens developed a shared sense of identity, history, and constitutional rights.",
                    "The French Revolution of 1789 introduced powerful unifying symbols—the tricolour flag, the ideas of la patrie (the fatherland) and le citoyen (the citizen), a uniform system of weights and measures, and abolition of internal customs duties. Napoleon's Civil Code of 1804 exported these administrative reforms across much of Europe.",
                    "Culture played an equally vital role through Romanticism. Poets, painters, and folklorists like the Grimm Brothers celebrated vernacular languages, folk songs, and shared heritage to awaken national consciousness among everyday people."
                )
            ),
            Chapter(
                id = "sst10_ch2",
                bookId = "book_sst_10",
                chapterNumber = 2,
                title = "Resources and Development",
                summary = "Classify natural resources, understand sustainable development, soil types of India, and conservation strategies.",
                estimatedMinutes = 18,
                keyPoints = listOf(
                    "Sustainable development meets present needs without compromising future generations.",
                    "Alluvial soil is the most widespread and fertile soil of northern Indian plains.",
                    "Black (Regur) soil retains moisture and is ideal for cotton cultivation."
                ),
                paragraphs = listOf(
                    "Everything available in our environment that can be used to satisfy our needs—provided it is technologically accessible, economically feasible, and culturally acceptable—is termed a Resource. Resources are not free gifts of nature alone; they are a function of human activities transforming materials through technology and institutions.",
                    "Indiscriminate exploitation of resources has led to global ecological crises including warming, ozone depletion, and land degradation. Sustainable economic development insists that development should take place without damaging the environment, ensuring equitable distribution for present and future generations."
                )
            )
        ),
        "book_sci_8" to listOf(
            Chapter(
                id = "sci8_ch1",
                bookId = "book_sci_8",
                chapterNumber = 1,
                title = "Force and Pressure",
                summary = "Understand pushes, pulls, contact vs non-contact forces, atmospheric pressure, and why wider straps feel lighter.",
                estimatedMinutes = 15,
                keyPoints = listOf(
                    "Force can change the speed, direction, or shape of an object.",
                    "Pressure = Force / Area (SI unit: Pascal, N/m²).",
                    "Smaller contact area creates higher pressure for the same applied force."
                ),
                paragraphs = listOf(
                    "Actions like kicking a football, opening a drawer, or lifting a school bag all involve either a push or a pull. In science, a push or a pull on an object is called a Force. Forces arise only due to an interaction between at least two objects.",
                    "Pressure is defined as the force acting per unit area of a surface: Pressure = Force / Area. This explains why school bags have wide shoulder straps—spreading the weight over a larger surface area reduces pressure on your shoulders, whereas a sharp knife cuts effortlessly because its thin edge concentrates force onto a tiny area."
                )
            ),
            Chapter(
                id = "sci8_ch2",
                bookId = "book_sci_8",
                chapterNumber = 2,
                title = "Microorganisms: Friend and Foe",
                summary = "Discover helpful bacteria, yeast, vaccines, nitrogen fixation, and safe food preservation methods.",
                estimatedMinutes = 16,
                keyPoints = listOf(
                    "Lactobacillus converts milk into curd; yeast drives baking via CO₂ fermentation.",
                    "Vaccines train the immune system using weakened or inactive microbes.",
                    "Pasteurization heats milk to 70°C for 15–30 seconds then chills rapidly."
                ),
                paragraphs = listOf(
                    "Living organisms around us that are too small to see with the unaided eye are called microorganisms or microbes. They are classified into four major groups: bacteria, fungi, protozoa, and some algae. Viruses are also microscopic but reproduce only inside host cells.",
                    "Far from being purely harmful, friendly microbes play essential roles in daily life. Lactobacillus multiplies in warm milk to make curd, yeast makes bread fluffy by releasing carbon dioxide bubbles during respiration, and Rhizobium bacteria in legume root nodules fix atmospheric nitrogen to enrich farm soil naturally."
                )
            )
        ),
        "book_math_5" to listOf(
            Chapter(
                id = "math5_ch1",
                bookId = "book_math_5",
                chapterNumber = 1,
                title = "Shapes and Angles",
                summary = "Spot right angles, acute angles, and obtuse angles in polygons, clocks, and everyday architecture.",
                estimatedMinutes = 12,
                keyPoints = listOf(
                    "A right angle looks like the corner of a book (90°).",
                    "An acute angle is smaller than a right angle (< 90°).",
                    "An obtuse angle is larger than a right angle (> 90° and < 180°)."
                ),
                paragraphs = listOf(
                    "Look around your classroom: the corner of your notebook, the edge of the blackboard, and the hands of a clock at 3:00 all form a Right Angle (90°). Angles tell us how wide two rays or edges open from a shared corner point called a vertex.",
                    "When a scissor opens just a little bit, it makes an Acute Angle—smaller than a right angle. When a laptop screen leans back past upright, it opens into an Obtuse Angle—wider than a right angle. Changing the angles of a polygon completely changes its shape even if its side lengths stay identical!"
                )
            ),
            Chapter(
                id = "math5_ch2",
                bookId = "book_math_5",
                chapterNumber = 2,
                title = "Parts and Wholes",
                summary = "Explore equivalent fractions, sharing chocolate bars fairly, and adding simple fractions visually.",
                estimatedMinutes = 14,
                keyPoints = listOf(
                    "A fraction represents equal parts of a whole: Numerator / Denominator.",
                    "1/2 = 2/4 = 3/6 = 4/8 are equivalent fractions representing the exact same share.",
                    "When sharing equally, more pieces means each piece is smaller."
                ),
                paragraphs = listOf(
                    "Imagine dividing a rectangular chocolate bar with 12 equal squares among friends. If you take 6 squares, you have 6/12 of the bar, which is exactly half (1/2). If your friend takes 3 squares, they have 3/12, which is one-quarter (1/4).",
                    "Fractions that look different yet cover the exact same portion of a whole are called Equivalent Fractions. Multiplying or dividing both the numerator (top) and denominator (bottom) by the same non-zero number always produces an equivalent fraction."
                )
            )
        ),
        "book_phy_12" to listOf(
            Chapter(
                id = "phy12_ch1",
                bookId = "book_phy_12",
                chapterNumber = 1,
                title = "Electric Charges and Fields",
                summary = "Derive Coulomb's Law, Electric Field intensity due to a dipole, and applications of Gauss's Law.",
                estimatedMinutes = 28,
                keyPoints = listOf(
                    "Quantization of charge: q = n·e where e = 1.6 × 10⁻¹⁹ C.",
                    "Coulomb's Law: F = (1 / 4πε₀) · (q₁q₂ / r²).",
                    "Gauss's Law: Total electric flux through a closed surface Φ = q_enclosed / ε₀."
                ),
                paragraphs = listOf(
                    "Electrostatics deals with the study of forces, fields, and potentials arising from static electric charges. Experimentally, charge is both conserved and quantized: any observable electric charge is always an integral multiple of the elementary charge e = 1.602 × 10⁻¹⁹ C.",
                    "Coulomb's Law quantifies the electrostatic force between two stationary point charges q₁ and q₂ separated by distance r in vacuum as F = k·|q₁q₂|/r², where k = 1/(4πε₀) ≈ 9 × 10⁹ N·m²/C². Like charges repel and unlike charges attract along the line joining them.",
                    "Gauss's Law states that the total electric flux passing through any closed Gaussian surface equals 1/ε₀ times the net charge enclosed within it. Using symmetry, Gauss's Law simplifies computing electric fields of infinitely long charged wires, uniformly charged sheets, and spherical shells."
                )
            ),
            Chapter(
                id = "phy12_ch2",
                bookId = "book_phy_12",
                chapterNumber = 2,
                title = "Current Electricity",
                summary = "Analyze drift velocity, temperature dependence of resistivity, Kirchhoff's rules, and Wheatstone bridge.",
                estimatedMinutes = 26,
                keyPoints = listOf(
                    "Drift velocity v_d = (eE / m)τ; Current I = n·e·A·v_d.",
                    "Kirchhoff's Junction Rule conserves charge; Loop Rule conserves energy.",
                    "Balanced Wheatstone Bridge condition: R₁/R₂ = R₃/R₄."
                ),
                paragraphs = listOf(
                    "In the absence of an electric field, free electrons in a metallic conductor move randomly with high thermal speeds so the net charge flow is zero. Applying an electric field E imparts a tiny average Drift Velocity v_d opposite to the field, establishing a steady electric current I = n·e·A·v_d.",
                    "For complex multi-loop networks that cannot be reduced to simple series or parallel combinations, Kirchhoff's Rules provide a systematic solution. The Junction Rule states that the sum of currents entering a node equals the sum leaving it, while the Loop Rule states that the algebraic sum of potential changes around any closed loop is zero."
                )
            )
        )
    )

    val starterStudyMaterials: List<StudyMaterial> = listOf(
        StudyMaterial(
            id = "mat_math_trig_formulas",
            title = "Trigonometry Complete Formula Sheet",
            category = "Formula Sheet",
            subject = "Mathematics",
            classLevel = "Class 10",
            chapterTitle = "Introduction to Trigonometry",
            description = "All ratios, standard angle table (0° to 90°), reciprocal relations, and Board-verified identities on one quick-scan sheet.",
            contentBody = """
                1. Core Ratios (SOH-CAH-TOA)
                • sin θ = Perpendicular / Hypotenuse
                • cos θ = Base / Hypotenuse
                • tan θ = Perpendicular / Base = sin θ / cos θ

                2. Reciprocal Identities
                • cosec θ = 1 / sin θ
                • sec θ = 1 / cos θ
                • cot θ = 1 / tan θ = cos θ / sin θ

                3. Fundamental Identities
                • sin²θ + cos²θ = 1
                • 1 + tan²θ = sec²θ
                • 1 + cot²θ = cosec²θ

                4. Standard Angle Quick Values
                • 0°:  sin=0, cos=1, tan=0
                • 30°: sin=1/2, cos=√3/2, tan=1/√3
                • 45°: sin=1/√2, cos=1/√2, tan=1
                • 60°: sin=√3/2, cos=1/2, tan=√3
                • 90°: sin=1, cos=0, tan=Not Defined
            """.trimIndent(),
            videoDurationMinutes = 0,
            premiumOnly = false,
            published = true
        ),
        StudyMaterial(
            id = "mat_sci_chem_notes",
            title = "Chemical Reactions — One-Page Revision Notes",
            category = "Revision Notes",
            subject = "Science",
            classLevel = "Class 10",
            chapterTitle = "Chemical Reactions & Equations",
            description = "High-yield equations, colour-change table, exothermic vs endothermic examples, and redox memory tricks.",
            contentBody = """
                KEY BALANCED EQUATIONS FOR BOARD EXAMS:
                1. Slaking of Lime (Exothermic Combination):
                   CaO(s) + H₂O(l) → Ca(OH)₂(aq) + Heat
                2. Thermal Decomposition of Ferrous Sulphate:
                   2FeSO₄(s) → Fe₂O₃(s) + SO₂(g) + SO₃(g) (Green → Reddish-brown)
                3. Photolytic Decomposition (Black & White Photography):
                   2AgCl(s) —sunlight→ 2Ag(s) + Cl₂(g) (White → Grey)
                4. Double Displacement (Precipitation):
                   Na₂SO₄(aq) + BaCl₂(aq) → BaSO₄(s)↓ (White ppt) + 2NaCl(aq)

                OIL RIG Memory Trick:
                • Oxidation Is Loss of electrons (or Gain of Oxygen)
                • Reduction Is Gain of electrons (or Loss of Oxygen)
            """.trimIndent(),
            videoDurationMinutes = 0,
            premiumOnly = false,
            published = true
        ),
        StudyMaterial(
            id = "mat_pyq_math_2025",
            title = "CBSE Class 10 Mathematics PYQ Set (2022–2025)",
            category = "Previous Year Papers",
            subject = "Mathematics",
            classLevel = "Class 10",
            chapterTitle = "Full Syllabus",
            description = "Most-repeated 3-mark and 5-mark board exam questions with marking-scheme step breakdowns.",
            contentBody = """
                Q1 (3 Marks — 2025 Board): Prove that 3 + 2√5 is irrational, given √5 is irrational.
                Solution Outline: Assume 3 + 2√5 = a/b (rational). Rearranging gives √5 = (a - 3b) / 2b. Since RHS is rational, √5 would be rational—contradiction!

                Q2 (3 Marks — 2024 Board): Find the value of k for which the quadratic equation kx(x - 2) + 6 = 0 has two equal real roots.
                Solution Outline: Expand to kx² - 2kx + 6 = 0. Set D = b² - 4ac = 0 ⇒ (-2k)² - 4(k)(6) = 0 ⇒ 4k(k - 6) = 0. Since k ≠ 0, k = 6.

                Q3 (5 Marks — 2023 Board): Prove that (sin A + cosec A)² + (cos A + sec A)² = 7 + tan²A + cot²A.
                Hint: Expand both squares, pair sin²A + cos²A = 1, simplify 2·sin A·cosec A = 2, and apply sec²A = 1 + tan²A, cosec²A = 1 + cot²A.
            """.trimIndent(),
            videoDurationMinutes = 0,
            premiumOnly = true,
            published = true
        ),
        StudyMaterial(
            id = "mat_eng_imp_q",
            title = "First Flight — Important Board Questions",
            category = "Important Questions",
            subject = "English",
            classLevel = "Class 10",
            chapterTitle = "A Letter to God & Nelson Mandela",
            description = "Curated value-based and extract-based 6-mark questions with model scoring points.",
            contentBody = """
                Q1. Explain the situational irony at the conclusion of 'A Letter to God'.
                Key Points:
                • Lencho had absolute faith that God would never deny his request or make a counting mistake.
                • The postmaster and staff generously pooled 70 pesos out of empathy.
                • Ironically, Lencho suspected those very benefactors of stealing 30 pesos and called them 'a bunch of crooks'.

                Q2. How did Nelson Mandela's understanding of freedom evolve from childhood to adulthood?
                Key Points:
                • As a boy, freedom meant running in fields and swimming in streams.
                • As a student, he sought transitory freedoms like staying out at night.
                • As a leader, he realized freedom is indivisible—the chains on any one of his people were the chains on all of them.
            """.trimIndent(),
            videoDurationMinutes = 0,
            premiumOnly = false,
            published = true
        ),
        StudyMaterial(
            id = "vid_math_real_numbers",
            title = "Mastering Real Numbers & Irrationality Proofs in 15 Mins",
            category = "Video Lecture",
            subject = "Mathematics",
            classLevel = "Class 10",
            chapterTitle = "Real Numbers",
            description = "Visual walkthrough of Fundamental Theorem of Arithmetic, HCF/LCM shortcuts, and foolproof board-exam proof templates.",
            contentBody = """
                VIDEO CHAPTER TIMELINE:
                [00:00] Why Prime Factorization is the DNA of Numbers
                [04:10] Fast HCF × LCM = a × b Problem-Solving Trick
                [08:30] Step-by-Step Proof that √2 and √3 are Irrational
                [12:45] Top 3 Board Exam Mistakes & How to Avoid Losing Marks

                TEACHER KEY TAKEAWAY:
                Always state 'let a and b be co-prime integers with b ≠ 0' on line 1 of an irrationality proof—that sentence carries 0.5 marking-scheme credit!
            """.trimIndent(),
            videoDurationMinutes = 15,
            premiumOnly = false,
            published = true
        ),
        StudyMaterial(
            id = "vid_sci_electricity",
            title = "Electricity & Ohm's Law Visual Circuit Lab",
            category = "Video Lecture",
            subject = "Science",
            classLevel = "Class 10",
            chapterTitle = "Electricity",
            description = "Animated breakdown of current flow, V-I graphs, series vs parallel combinations, and numerical solving strategies.",
            contentBody = """
                VIDEO CHAPTER TIMELINE:
                [00:00] Visualizing Charge, Current & Potential Difference
                [05:20] Deriving V = IR & Reading Slope of V-I Graphs
                [10:40] Series vs Parallel Resistors — Household Wiring Demo
                [16:15] Solved 5-Mark Numerical on Electric Power & Energy Bill (kWh)

                PRO TIP FOR NUMERICALS:
                1 kWh = 1 commercial unit of electricity = 3.6 × 10⁶ Joules. Always convert Watts to Kilowatts before multiplying by hours!
            """.trimIndent(),
            videoDurationMinutes = 20,
            premiumOnly = false,
            published = true
        ),
        StudyMaterial(
            id = "vid_hin_surdas",
            title = "सूरदास के पद — सरल भावार्थ और काव्य सौंदर्य",
            category = "Video Lecture",
            subject = "Hindi",
            classLevel = "Class 10",
            chapterTitle = "सूरदास के पद",
            description = "बोर्ड परीक्षा के लिए चारों पदों की पंक्ति-दर-पंक्ति सरल व्याख्या और मुख्य अलंकारों की पहचान।",
            contentBody = """
                वीडियो अध्याय सारांश:
                [00:00] भ्रमरगीत की पृष्ठभूमि और प्रसंग
                [03:45] पद 1: 'ऊधौ, तुम हौ अति बड़भागी' — व्यंग्य का अर्थ
                [08:15] पद 2 व 3: मन की मन ही माँझ रही व हमारे हरि हारिल की लकरी
                [12:30] परीक्षा में पूरे अंक लाने के लिए उत्तर लेखन टिप्स
            """.trimIndent(),
            videoDurationMinutes = 14,
            premiumOnly = false,
            published = true
        )
    )

    val starterAssessments: List<Assessment> = listOf(
        Assessment(
            id = "quiz_daily_mix",
            title = "Daily Smart Practice Quiz",
            subject = "Mathematics",
            classLevel = "Class 10",
            mode = "QUIZ",
            category = "Daily Quiz",
            durationMinutes = 8,
            totalMarks = 5,
            instructions = "5 high-yield questions combining MCQ, True/False, and Fill-in-the-Blanks. Instant explanations included!",
            premiumOnly = false,
            published = true,
            questions = listOf(
                QuestionItem(
                    prompt = "If HCF(26, 169) = 13, what is LCM(26, 169)?",
                    type = "MCQ",
                    options = listOf("169", "338", "260", "507"),
                    correctIndex = 1,
                    explanation = "Using HCF × LCM = a × b: LCM = (26 × 169) / 13 = 2 × 169 = 338.",
                    topic = "Real Numbers",
                    marks = 1
                ),
                QuestionItem(
                    prompt = "What is the value of sin² 30° + cos² 30°?",
                    type = "MCQ",
                    options = listOf("0", "1/2", "1", "√3/2"),
                    correctIndex = 2,
                    explanation = "By the fundamental identity sin²θ + cos²θ = 1 for any angle θ.",
                    topic = "Trigonometry",
                    marks = 1
                ),
                QuestionItem(
                    prompt = "True or False: A quadratic equation with discriminant D < 0 has two equal real roots.",
                    type = "TRUE_FALSE",
                    options = listOf("True", "False"),
                    correctIndex = 1,
                    explanation = "False! Equal real roots occur when D = 0. When D < 0, the equation has no real roots.",
                    topic = "Quadratic Equations",
                    marks = 1
                ),
                QuestionItem(
                    prompt = "Fill in the blank: The SI unit of electric resistance is ____ (write word, e.g. Ohm).",
                    type = "FILL_BLANK",
                    options = emptyList(),
                    correctIndex = 0,
                    correctText = "ohm",
                    explanation = "Electrical resistance is measured in Ohms (Ω), named after Georg Simon Ohm.",
                    topic = "Electricity",
                    marks = 1
                ),
                QuestionItem(
                    prompt = "Which gas is released during photosynthesis when water molecules split?",
                    type = "MCQ",
                    options = listOf("Carbon dioxide", "Nitrogen", "Oxygen", "Methane"),
                    correctIndex = 2,
                    explanation = "Photolysis of water during photosynthesis releases Oxygen (O₂).",
                    topic = "Life Processes",
                    marks = 1
                )
            )
        ),
        Assessment(
            id = "quiz_sci_ch1",
            title = "Science Ch. 1: Chemical Reactions MCQ",
            subject = "Science",
            classLevel = "Class 10",
            mode = "QUIZ",
            category = "Chapter MCQ",
            durationMinutes = 10,
            totalMarks = 4,
            instructions = "Test your mastery of balancing equations, reaction types, and redox concepts.",
            premiumOnly = false,
            published = true,
            questions = listOf(
                QuestionItem(
                    prompt = "Reaction of Calcium Oxide (quicklime) with water to form slaked lime is an example of:",
                    type = "MCQ",
                    options = listOf("Endothermic decomposition", "Exothermic combination", "Displacement", "Neutralization"),
                    correctIndex = 1,
                    explanation = "CaO + H₂O → Ca(OH)₂ + Heat combines two reactants into one product while releasing heat.",
                    topic = "Reaction Types",
                    marks = 1
                ),
                QuestionItem(
                    prompt = "In the reaction CuO + H₂ → Cu + H₂O, which substance is reduced?",
                    type = "MCQ",
                    options = listOf("H₂", "CuO", "H₂O", "Cu"),
                    correctIndex = 1,
                    explanation = "CuO loses oxygen to form Cu, so Copper(II) oxide (CuO) is reduced.",
                    topic = "Redox Reactions",
                    marks = 1
                ),
                QuestionItem(
                    prompt = "True or False: Silver chloride turns grey in sunlight due to photolytic decomposition.",
                    type = "TRUE_FALSE",
                    options = listOf("True", "False"),
                    correctIndex = 0,
                    explanation = "True! Sunlight decomposes white AgCl into grey silver metal and chlorine gas.",
                    topic = "Decomposition",
                    marks = 1
                ),
                QuestionItem(
                    prompt = "Chips packets are flushed with which inert gas to prevent rancidity?",
                    type = "MCQ",
                    options = listOf("Oxygen", "Hydrogen", "Nitrogen", "Chlorine"),
                    correctIndex = 2,
                    explanation = "Nitrogen displaces oxygen inside packets to stop fats and oils from oxidizing.",
                    topic = "Oxidation Effects",
                    marks = 1
                )
            )
        ),
        Assessment(
            id = "exam_board_mock_1",
            title = "CBSE Class 10 Online Board Mock Exam",
            subject = "Science",
            classLevel = "Class 10",
            mode = "EXAM",
            category = "Mock Exam",
            durationMinutes = 15,
            totalMarks = 20,
            instructions = "• All 5 questions carry 4 marks each (Total: 20 Marks).\n• No negative marking in this board mock test.\n• Use the Question Palette to jump or Mark for Review.\n• Exam auto-submits when the countdown reaches 00:00.",
            premiumOnly = false,
            published = true,
            questions = listOf(
                QuestionItem(
                    prompt = "Two resistors of 6 Ω and 3 Ω are connected in parallel. What is their equivalent resistance?",
                    type = "MCQ",
                    options = listOf("9 Ω", "4.5 Ω", "2 Ω", "1.5 Ω"),
                    correctIndex = 2,
                    explanation = "1/R_p = 1/6 + 1/3 = 3/6 = 1/2 ⇒ R_p = 2 Ω.",
                    topic = "Electricity",
                    marks = 4
                ),
                QuestionItem(
                    prompt = "Which part of the human kidney performs the primary filtration of blood to form urine?",
                    type = "MCQ",
                    options = listOf("Alveoli", "Nephron", "Neuron", "Villi"),
                    correctIndex = 1,
                    explanation = "Each kidney contains millions of nephrons that filter nitrogenous wastes like urea.",
                    topic = "Life Processes",
                    marks = 4
                ),
                QuestionItem(
                    prompt = "For the quadratic equation 2x² - 4x + 2 = 0, the roots are:",
                    type = "MCQ",
                    options = listOf("Real and distinct", "Real and equal", "No real roots", "Irrational"),
                    correctIndex = 1,
                    explanation = "D = b² - 4ac = (-4)² - 4(2)(2) = 16 - 16 = 0, giving two equal real roots x = 1.",
                    topic = "Quadratic Equations",
                    marks = 4
                ),
                QuestionItem(
                    prompt = "True or False: In domestic wiring, household appliances are connected in parallel so each gets 220 V.",
                    type = "TRUE_FALSE",
                    options = listOf("True", "False"),
                    correctIndex = 0,
                    explanation = "True! Parallel connections ensure uniform voltage and independent switching.",
                    topic = "Domestic Circuits",
                    marks = 4
                ),
                QuestionItem(
                    prompt = "Who led the Prussian movement for the unification of Germany in 1871?",
                    type = "MCQ",
                    options = listOf("Giuseppe Mazzini", "Otto von Bismarck", "Napoleon Bonaparte", "Metternich"),
                    correctIndex = 1,
                    explanation = "Prussian Chief Minister Otto von Bismarck was the chief architect of German unification.",
                    topic = "Nationalism in Europe",
                    marks = 4
                )
            )
        )
    )

    val starterAnnouncements: List<Announcement> = listOf(
        Announcement(
            id = "ann_welcome",
            title = "2026–27 Revised Board Textbooks Live!",
            body = "Explore updated CBSE & ICSE chapters with AI 'Explain This Paragraph' built right into the Textbook Reader.",
            category = "NEW_BOOK",
            targetAudience = "ALL",
            published = true
        ),
        Announcement(
            id = "ann_mock_exam",
            title = "Weekly Online Mock Exam Open",
            body = "Attempt the timed 20-mark Board Mock Exam under Practice to receive a detailed weak-topic diagnosis.",
            category = "EXAM",
            targetAudience = "CLASS_10",
            published = true
        ),
        Announcement(
            id = "ann_notes",
            title = "Trigonometry & Chemistry Revision Sheets Added",
            body = "Save the one-page formula sheet and balanced equations checklist to your profile for quick pre-exam revision.",
            category = "NOTES",
            targetAudience = "ALL",
            published = true
        )
    )
}
