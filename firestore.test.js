const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const STUDENT_A = "student_a_uid";
const STUDENT_B = "student_b_uid";
const ADMIN_UID = "admin_owner_uid";
const ADMIN_EMAIL = "buildolive4@gmail.com";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function pastDate(msOffset = 2000) {
  return new Date(Date.now() - msOffset);
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated users cannot read or write any collection", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(STUDENT_A).get());
  await assertFails(
    unauthDb.collection("textbooks").where("published", "==", true).get()
  );
  await assertFails(
    unauthDb.collection("studyMaterials").where("published", "==", true).get()
  );
  await assertFails(
    unauthDb.collection("assessments").where("published", "==", true).get()
  );
  await assertFails(
    unauthDb.collection("announcements").where("published", "==", true).get()
  );
});

test("Student profile CRUD, onboarding update, and strict cross-user isolation", async () => {
  const dbA = testEnv.authenticatedContext(STUDENT_A).firestore();
  const dbB = testEnv.authenticatedContext(STUDENT_B).firestore();
  const now = pastDate(5000);

  // Valid create mirroring Kotlin UserProfileRepository
  await assertSucceeds(
    dbA.collection("users").doc(STUDENT_A).set({
      userId: STUDENT_A,
      displayName: "Aarav Sharma",
      email: "aarav@school.edu",
      board: "CBSE",
      classLevel: "Class 10",
      medium: "English",
      academicSession: "2026-27",
      subjects: ["Mathematics", "Science", "English"],
      onboardingDone: true,
      planTier: "FREE",
      studyMinutesTotal: 45,
      streakDays: 3,
      aiQueriesToday: 1,
      createdAt: now,
      updatedAt: now,
    })
  );

  // Valid partial update (mutable fields only, createdAt preserved)
  await assertSucceeds(
    dbA.collection("users").doc(STUDENT_A).update({
      board: "ICSE",
      planTier: "MONTHLY",
      studyMinutesTotal: 90,
      updatedAt: pastDate(1000),
    })
  );

  // Reject ghost field / role escalation
  await assertFails(
    dbA.collection("users").doc(STUDENT_A).update({
      role: "ADMIN",
      updatedAt: pastDate(500),
    })
  );

  // Reject future timestamp
  await assertFails(
    dbA.collection("users").doc(STUDENT_A).update({
      displayName: "Future User",
      updatedAt: new Date(Date.now() + 60000),
    })
  );

  // Reject immutable createdAt mutation
  await assertFails(
    dbA.collection("users").doc(STUDENT_A).update({
      createdAt: pastDate(100),
      updatedAt: pastDate(100),
    })
  );

  // Cross-user read & write denied (PII isolation)
  await assertFails(dbB.collection("users").doc(STUDENT_A).get());
  await assertFails(dbB.collection("users").get());
  await assertFails(
    dbB.collection("users").doc(STUDENT_A).update({
      displayName: "Hijacked",
      updatedAt: pastDate(1000),
    })
  );
});

test("User subcollections: readingProgress, personalNotes, studyTasks, quizAttempts, aiMessages, savedMaterials", async () => {
  const dbA = testEnv.authenticatedContext(STUDENT_A).firestore();
  const dbB = testEnv.authenticatedContext(STUDENT_B).firestore();
  const now = pastDate(4000);

  // Reading progress create & update
  await assertSucceeds(
    dbA
      .collection("users")
      .doc(STUDENT_A)
      .collection("readingProgress")
      .doc("book_math_10")
      .set({
        userId: STUDENT_A,
        bookId: "book_math_10",
        bookTitle: "Mathematics Class 10",
        subject: "Mathematics",
        lastChapterId: "ch_1",
        lastChapterNumber: 1,
        lastChapterTitle: "Real Numbers",
        lastPageIndex: 1,
        completedChapters: ["ch_1"],
        progressPercent: 25,
        bookmarkedPages: ["ch_1:1"],
        highlightedParagraphs: ["ch_1:0"],
        createdAt: now,
        updatedAt: now,
      })
  );

  await assertSucceeds(
    dbA
      .collection("users")
      .doc(STUDENT_A)
      .collection("readingProgress")
      .doc("book_math_10")
      .update({
        lastPageIndex: 2,
        progressPercent: 50,
        updatedAt: pastDate(1000),
      })
  );

  // Personal notes create, list, update, delete
  await assertSucceeds(
    dbA
      .collection("users")
      .doc(STUDENT_A)
      .collection("personalNotes")
      .doc("note_1")
      .set({
        userId: STUDENT_A,
        bookId: "book_math_10",
        bookTitle: "Mathematics Class 10",
        chapterId: "ch_1",
        chapterTitle: "Real Numbers",
        content: "Remember Euclid's division lemma: a = bq + r.",
        excerpt: "For any two positive integers a and b...",
        createdAt: now,
        updatedAt: now,
      })
  );

  await assertSucceeds(
    dbA.collection("users").doc(STUDENT_A).collection("personalNotes").get()
  );

  // Study task create & toggle completion
  await assertSucceeds(
    dbA
      .collection("users")
      .doc(STUDENT_A)
      .collection("studyTasks")
      .doc("task_1")
      .set({
        userId: STUDENT_A,
        title: "Revise Trigonometry Identities",
        subject: "Mathematics",
        dueDate: "2026-11-28",
        targetMinutes: 45,
        completed: false,
        createdAt: now,
        updatedAt: now,
      })
  );

  await assertSucceeds(
    dbA
      .collection("users")
      .doc(STUDENT_A)
      .collection("studyTasks")
      .doc("task_1")
      .update({
        completed: true,
        updatedAt: pastDate(1000),
      })
  );

  // Quiz attempt create (immutable)
  await assertSucceeds(
    dbA
      .collection("users")
      .doc(STUDENT_A)
      .collection("quizAttempts")
      .doc("attempt_1")
      .set({
        userId: STUDENT_A,
        quizId: "quiz_math_ch1",
        quizTitle: "Real Numbers Mastery Quiz",
        subject: "Mathematics",
        mode: "QUIZ",
        totalQuestions: 5,
        correctCount: 4,
        incorrectCount: 1,
        unattemptedCount: 0,
        scorePercent: 80,
        durationSeconds: 180,
        weakTopics: ["Irrational Proofs"],
        createdAt: now,
      })
  );

  // AI chat message create
  await assertSucceeds(
    dbA
      .collection("users")
      .doc(STUDENT_A)
      .collection("aiMessages")
      .doc("msg_1")
      .set({
        userId: STUDENT_A,
        sessionId: "default_session",
        sender: "USER",
        language: "English",
        subject: "Mathematics",
        message: "Explain Euclid's division lemma in simple words.",
        createdAt: now,
      })
  );

  // Saved material create
  await assertSucceeds(
    dbA
      .collection("users")
      .doc(STUDENT_A)
      .collection("savedMaterials")
      .doc("mat_1")
      .set({
        userId: STUDENT_A,
        materialId: "mat_1",
        itemType: "NOTE",
        title: "Trigonometry Formula Sheet",
        subject: "Mathematics",
        createdAt: now,
      })
  );

  // Cross-user isolation on all subcollections
  await assertFails(
    dbB.collection("users").doc(STUDENT_A).collection("readingProgress").get()
  );
  await assertFails(
    dbB.collection("users").doc(STUDENT_A).collection("personalNotes").get()
  );
  await assertFails(
    dbB.collection("users").doc(STUDENT_A).collection("studyTasks").get()
  );
  await assertFails(
    dbB.collection("users").doc(STUDENT_A).collection("aiMessages").get()
  );
});

test("Shared catalog: Admin can publish textbooks/chapters/materials/assessments/announcements; Students read only published with query filter", async () => {
  const adminDb = testEnv
    .authenticatedContext(ADMIN_UID, {
      email: ADMIN_EMAIL,
      email_verified: true,
    })
    .firestore();
  const spoofAdminDb = testEnv
    .authenticatedContext("spoof_uid", {
      email: ADMIN_EMAIL,
      email_verified: false,
    })
    .firestore();
  const studentDb = testEnv.authenticatedContext(STUDENT_A).firestore();
  const now = pastDate(3000);

  const bookPayload = {
    title: "Mathematics Class 10",
    subject: "Mathematics",
    classLevel: "Class 10",
    category: "Class 9-10",
    board: "CBSE",
    medium: "English",
    academicYear: "2026-27",
    description: "Complete CBSE Class 10 Mathematics digital textbook.",
    chapterCount: 2,
    premiumOnly: false,
    published: true,
    accentHex: "#183B56",
    createdBy: ADMIN_UID,
    createdAt: now,
    updatedAt: now,
  };

  // Spoofed unverified admin email MUST fail
  await assertFails(
    spoofAdminDb.collection("textbooks").doc("book_1").set({
      ...bookPayload,
      createdBy: "spoof_uid",
    })
  );

  // Student cannot publish textbooks
  await assertFails(
    studentDb.collection("textbooks").doc("book_1").set({
      ...bookPayload,
      createdBy: STUDENT_A,
    })
  );

  // Verified Admin can create textbook & chapter
  await assertSucceeds(
    adminDb.collection("textbooks").doc("book_1").set(bookPayload)
  );
  await assertSucceeds(
    adminDb
      .collection("textbooks")
      .doc("book_1")
      .collection("chapters")
      .doc("ch_1")
      .set({
        bookId: "book_1",
        chapterNumber: 1,
        title: "Real Numbers",
        summary: "Foundations of number theory and Euclid's lemma.",
        paragraphs: ["Every composite number can be expressed as a product of primes."],
        keyPoints: ["Fundamental Theorem of Arithmetic"],
        estimatedMinutes: 20,
        published: true,
        createdBy: ADMIN_UID,
        createdAt: now,
        updatedAt: now,
      })
  );

  // Student filtered query matching `published == true` SUCCEEDS
  await assertSucceeds(
    studentDb.collection("textbooks").where("published", "==", true).get()
  );
  await assertSucceeds(
    studentDb
      .collection("textbooks")
      .doc("book_1")
      .collection("chapters")
      .where("published", "==", true)
      .get()
  );

  // Student unfiltered query on shared collection MUST FAIL (rules are not filters!)
  await assertFails(studentDb.collection("textbooks").get());

  // Admin can create studyMaterial, assessment, announcement
  await assertSucceeds(
    adminDb.collection("studyMaterials").doc("mat_1").set({
      title: "Real Numbers Short Notes",
      category: "Short Notes",
      subject: "Mathematics",
      classLevel: "Class 10",
      chapterTitle: "Real Numbers",
      description: "Quick revision sheet for board exams.",
      contentBody: "HCF x LCM = Product of two positive integers.",
      videoDurationMinutes: 0,
      premiumOnly: false,
      published: true,
      createdBy: ADMIN_UID,
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    adminDb.collection("assessments").doc("assess_1").set({
      title: "Class 10 Science Mock Exam",
      subject: "Science",
      classLevel: "Class 10",
      mode: "EXAM",
      category: "Mock Exam",
      durationMinutes: 30,
      totalMarks: 20,
      instructions: "Read each question carefully. Auto-submits on timeout.",
      questions: [
        {
          prompt: "What is the SI unit of electric current?",
          type: "MCQ",
          options: ["Volt", "Ampere", "Ohm", "Watt"],
          correctIndex: 1,
          explanation: "Electric current is measured in Amperes (A).",
          topic: "Electricity",
          marks: 4,
        },
      ],
      premiumOnly: false,
      published: true,
      createdBy: ADMIN_UID,
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    adminDb.collection("announcements").doc("ann_1").set({
      title: "New Board Exam Sample Papers Published",
      body: "Practice the updated 2026-27 sample papers in the Exam Hub.",
      category: "EXAM",
      targetAudience: "ALL",
      published: true,
      createdBy: ADMIN_UID,
      createdAt: now,
    })
  );

  // Student filtered reads succeed; unfiltered reads fail
  await assertSucceeds(
    studentDb.collection("studyMaterials").where("published", "==", true).get()
  );
  await assertSucceeds(
    studentDb.collection("assessments").where("published", "==", true).get()
  );
  await assertSucceeds(
    studentDb.collection("announcements").where("published", "==", true).get()
  );
  await assertFails(studentDb.collection("assessments").get());
});
