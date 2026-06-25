package viewmodel

import model.*
import java.util.Locale

class GainOSViewModel {

    // ── Grupos musculares ──────────────────────────────────────────
    val muscleGroups = listOf(
        MuscleGroup("mg1", "Peito",   "▣"),
        MuscleGroup("mg2", "Costas",  "▤"),
        MuscleGroup("mg3", "Pernas",  "▥"),
        MuscleGroup("mg4", "Ombros",  "▦"),
        MuscleGroup("mg5", "Bíceps",  "▧"),
        MuscleGroup("mg6", "Tríceps", "▨"),
        MuscleGroup("mg7", "Core",    "▩")
    )

    // ── Exercícios ─────────────────────────────────────────────────
    private val allExercises = listOf(
        Exercise("e1",  "Supino Reto",          "Barra na largura dos ombros, desça até o peito e empurre.",              "mg1", 4, "6–10",     90,  2),
        Exercise("e2",  "Crucifixo Inclinado",  "Halteres com leve flexão do cotovelo, amplitude máxima.",                "mg1", 3, "10–12",    60,  1),
        Exercise("e3",  "Peck Deck",            "Máquina. Mantém tensão no peito durante todo o movimento.",              "mg1", 3, "12–15",    60,  1),
        Exercise("e4",  "Barra Fixa",           "Pegada pronada na largura dos ombros. Peito até a barra.",               "mg2", 4, "até falha", 90, 2),
        Exercise("e5",  "Remada Curvada",       "Costas retas, puxa a barra até o umbigo com cotovelos fechados.",        "mg2", 4, "8–10",     90,  2),
        Exercise("e6",  "Pulldown",             "Polia alta. Puxa até a clavícula, escápulas unidas.",                    "mg2", 3, "10–12",    60,  1),
        Exercise("e7",  "Agachamento Livre",    "Barra nas costas, joelhos na linha dos pés, desça abaixo do paralelo.",  "mg3", 4, "6–8",     120,  3),
        Exercise("e8",  "Leg Press 45°",        "Pés na largura do quadril. Não trave os joelhos no topo.",               "mg3", 3, "10–12",    75,  1),
        Exercise("e9",  "Cadeira Extensora",    "Extensão completa, pausa no topo, descida controlada.",                  "mg3", 3, "12–15",    60,  1),
        Exercise("e10", "Desenvolvimento",      "Barra ou halteres na altura das orelhas, empurra vertical.",             "mg4", 4, "8–10",     90,  2),
        Exercise("e11", "Elevação Lateral",     "Halteres leves, cotovelos levemente flexionados, até a linha do ombro.", "mg4", 3, "12–15",    45,  1),
        Exercise("e12", "Rosca Direta",         "Barra reta ou EZ. Não balança o tronco.",                                "mg5", 3, "10–12",    60,  1),
        Exercise("e13", "Rosca Concentrada",    "Cotovelo no joelho, amplitude total. Foca na contração.",                "mg5", 3, "12–15",    45,  1),
        Exercise("e14", "Tríceps Testa",        "Barra EZ deitado, desce até a testa, cotovelos fixos.",                  "mg6", 3, "10–12",    60,  1),
        Exercise("e15", "Tríceps Polia",        "Corda ou barra reta. Cotovelos junto ao corpo, extende completo.",       "mg6", 3, "12–15",    45,  1),
        Exercise("e16", "Prancha",              "Cotovelos e pés. Quadril neutro, respira. Segura o tempo.",              "mg7", 3, "30–60s",   45,  1),
        Exercise("e17", "Abdominal Roda",       "Joelhos no chão, roda até o limite sem afundar o lombo.",                "mg7", 3, "8–12",     60,  2)
    )

    // ── Planos de treino ───────────────────────────────────────────
    val plans = listOf(
        WorkoutPlan("pl1", "Push Pull Legs",  "Hipertrofia", 6, 12,
            listOf("e1","e2","e3","e4","e5","e6","e7","e8","e9","e10","e11","e14","e15","e12","e13")),
        WorkoutPlan("pl2", "Full Body A/B",   "Força",       4, 8,
            listOf("e1","e4","e7","e10","e12","e14","e16")),
        WorkoutPlan("pl3", "Core & Shape",    "Definição",   5, 10,
            listOf("e2","e3","e6","e8","e9","e11","e13","e15","e16","e17"))
    )

    var activePlan: WorkoutPlan? = null
        private set

    var selectedMuscleGroup: MuscleGroup? = null
        private set

    var filteredExercises: List<Exercise> = allExercises
        private set

    var selectedExercise: Exercise? = null
        private set

    var searchQuery = ""
        private set

    // ── Sessão atual ───────────────────────────────────────────────
    val currentSessionLogs = mutableListOf<SetLog>()

    // ── Histórico ──────────────────────────────────────────────────
    val sessionHistory = mutableListOf(
        WorkoutSession("s1", "Push Pull Legs", "12 jun 2026", 58, 16, "Supino bateu PR: 100kg"),
        WorkoutSession("s2", "Full Body A/B",  "10 jun 2026", 45, 12),
        WorkoutSession("s3", "Push Pull Legs", "08 jun 2026", 62, 18),
        WorkoutSession("s4", "Full Body A/B",  "06 jun 2026", 50, 14)
    )

    // ── Recordes pessoais ──────────────────────────────────────────
    val personalRecords = mutableListOf(
        PersonalRecord("Supino Reto",    100.0, 5,  "12 jun 2026"),
        PersonalRecord("Agachamento",    120.0, 3,  "08 jun 2026"),
        PersonalRecord("Barra Fixa",      0.0,  12, "10 jun 2026")
    )

    // ── Perfil ────────────────────────────────────────────────────
    val profile = UserProfile(
        name      = "Lucas Ferreira",
        age       = 27,
        weightKg  = 82.0,
        heightCm  = 178,
        goal      = "Ganhar massa"
    )

    // ── Streak e estatísticas ─────────────────────────────────────
    val streakDays = 9
    val weeklyGoal = 4       // treinos por semana
    val weekDone   = 3       // feitos essa semana

    // ── Ações ─────────────────────────────────────────────────────
    fun selectPlan(plan: WorkoutPlan) { activePlan = plan }
    fun clearPlan()                   { activePlan = null }

    fun selectMuscleGroup(mg: MuscleGroup?) {
        selectedMuscleGroup = mg
        applyFilters()
    }

    fun setSearchQuery(q: String) {
        searchQuery = q
        applyFilters()
    }

    private fun applyFilters() {
        val q  = searchQuery.trim().lowercase()
        val mg = selectedMuscleGroup
        var list = allExercises
        if (mg != null) list = list.filter { it.muscleGroupId == mg.id }
        if (q.isNotEmpty()) list = list.filter {
            it.name.lowercase().contains(q) || it.description.lowercase().contains(q)
        }
        filteredExercises = list
    }

    fun selectExercise(ex: Exercise) { selectedExercise = ex }

    fun logSet(exerciseId: String, reps: Int, weightKg: Double) {
        currentSessionLogs.add(SetLog(exerciseId, reps, weightKg))
    }

    fun finishSession(durationMinutes: Int): WorkoutSession {
        val planName = activePlan?.name ?: "Treino livre"
        val session  = WorkoutSession(
            id              = "s${sessionHistory.size + 1}",
            planName        = planName,
            date            = "Hoje",
            durationMinutes = durationMinutes,
            totalSets       = currentSessionLogs.size
        )
        sessionHistory.add(0, session)

        // Checa novos recordes
        currentSessionLogs.groupBy { it.exerciseId }.forEach { (exId, logs) ->
            val ex      = allExercises.find { it.id == exId } ?: return@forEach
            val bestSet = logs.maxByOrNull { it.weightKg } ?: return@forEach
            val pr      = personalRecords.find { it.exerciseName == ex.name }
            if (pr == null || bestSet.weightKg > pr.weightKg) {
                personalRecords.removeAll { it.exerciseName == ex.name }
                personalRecords.add(PersonalRecord(ex.name, bestSet.weightKg, bestSet.reps, "Hoje"))
            }
        }

        currentSessionLogs.clear()
        return session
    }

    fun updateWeight(newKg: Double) { profile.weightKg = newKg }
    fun updateGoal(newGoal: String) { profile.goal = newGoal }

    fun getMuscleGroupName(id: String) = muscleGroups.find { it.id == id }?.name ?: id

    fun getDifficultyLabel(d: Int) = when (d) {
        1    -> "Iniciante"
        2    -> "Intermediário"
        else -> "Avançado"
    }

    fun formatWeight(kg: Double) = if (kg == 0.0) "peso corporal" else "%.1f kg".format(Locale.US, kg)

    fun imc(): Double = profile.weightKg / ((profile.heightCm / 100.0) * (profile.heightCm / 100.0))

    fun weeklyProgress(): String {
        val bars = (1..weeklyGoal).joinToString("") { if (it <= weekDone) "█" else "░" }
        return "$bars  $weekDone/$weeklyGoal"
    }
}