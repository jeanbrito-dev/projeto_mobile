package model

data class MuscleGroup(
    val id: String,
    val name: String,
    val icon: String
)

data class Exercise(
    val id: String,
    val name: String,
    val description: String,
    val muscleGroupId: String,
    val sets: Int,
    val reps: String,       // ex: "8–12" ou "até falha"
    val restSeconds: Int,
    val difficulty: Int     // 1–3
)

data class WorkoutPlan(
    val id: String,
    val name: String,
    val goal: String,       // ex: "Hipertrofia", "Definição"
    val daysPerWeek: Int,
    val durationWeeks: Int,
    val exerciseIds: List<String>
)

data class WorkoutSession(
    val id: String,
    val planName: String,
    val date: String,
    val durationMinutes: Int,
    val totalSets: Int,
    val notes: String = ""
)

data class SetLog(
    val exerciseId: String,
    var reps: Int,
    var weightKg: Double
)

data class UserProfile(
    var name: String,
    var age: Int,
    var weightKg: Double,
    var heightCm: Int,
    var goal: String        // ex: "Ganhar massa", "Perder gordura"
)

data class PersonalRecord(
    val exerciseName: String,
    val weightKg: Double,
    val reps: Int,
    val date: String
)