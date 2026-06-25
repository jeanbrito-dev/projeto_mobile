import model.*
import viewmodel.GainOSViewModel

// ─────────────────────────────────────────────────────────────────
//  GainOS — app de treinos
//  Tema: "Ferro & Concreto"
//  Inspiração: Strong App + Whoop + Apple Fitness
//  Paleta ANSI-256:
//    CHALK   = branco giz         (títulos primários)
//    STEEL   = cinza aço claro    (texto normal)
//    SLATE   = cinza ardósia      (metadados / dim)
//    LIME    = verde-lima         (progresso / ok / streak)
//    AMBER   = âmbar              (avisos / dificuldade média)
//    CRIMSON = vermelho-escuro    (erros / avançado)
//    GOLD    = dourado            (PRs / recordes)
// ─────────────────────────────────────────────────────────────────

const val R       = "\u001b[0m"
const val BOLD    = "\u001b[1m"
const val ITALIC  = "\u001b[3m"
const val DIM     = "\u001b[2m"

const val CHALK   = "\u001b[38;5;255m"
const val STEEL   = "\u001b[38;5;252m"
const val SLATE   = "\u001b[38;5;244m"
const val LIME    = "\u001b[38;5;154m"
const val AMBER   = "\u001b[38;5;214m"
const val CRIMSON = "\u001b[38;5;160m"
const val GOLD    = "\u001b[38;5;220m"

val RULE  = "${SLATE}${"─".repeat(60)}${R}"
val HEAVY = "${SLATE}${"═".repeat(60)}${R}"

enum class Screen { DASHBOARD, EXERCISES, EXERCISE_DETAIL, PLANS, ACTIVE_SESSION, PROFILE }

fun main() {
    val vm     = GainOSViewModel()
    var screen = Screen.DASHBOARD
    var fb     = ""
    var fbOk   = true
    var sessionStartMin = 0

    while (true) {
        cls()

        // ── Feedback bar ──────────────────────────────────────────
        if (fb.isNotEmpty()) {
            val dot = if (fbOk) "${LIME}◆${R}" else "${CRIMSON}◆${R}"
            val txt = if (fbOk) "${LIME}${BOLD}$fb${R}" else "${CRIMSON}${BOLD}$fb${R}"
            println("$dot  $txt")
            println(RULE)
            fb = ""
        }

        when (screen) {

            // ══════════════════════════════════════════════════════
            Screen.DASHBOARD -> {
                println()
                println("${CHALK}${BOLD}GAINOS${R}  ${SLATE}${ITALIC}seu diário de ferro${R}")
                println()
                println(HEAVY)
                println()

                // Streak e progresso semanal
                println("  ${LIME}${BOLD}${vm.streakDays} dias seguidos${R}  ${SLATE}· semana: ${vm.weeklyProgress()}${R}")
                println()

                // Plano ativo
                val plan = vm.activePlan
                if (plan != null) {
                    println("  ${SLATE}plano ativo${R}")
                    println("  ${CHALK}${BOLD}${plan.name}${R}  ${SLATE}${plan.goal} · ${plan.daysPerWeek}×/sem · ${plan.durationWeeks} sem${R}")
                    println()
                    println("  ${LIME}[T] iniciar treino de hoje${R}  ${SLATE}[X] remover plano${R}")
                } else {
                    println("  ${SLATE}nenhum plano ativo.${R}")
                    println("  ${AMBER}[P] escolher um plano de treino${R}")
                }
                println()
                println(RULE)

                // Histórico recente
                println()
                println("  ${SLATE}${ITALIC}últimas sessões${R}")
                println()
                vm.sessionHistory.take(3).forEach { s ->
                    val dur = "${s.durationMinutes} min"
                    println("  ${CHALK}${BOLD}${s.planName}${R}  ${SLATE}${s.date}  ·  ${s.totalSets} séries  ·  $dur${R}")
                    if (s.notes.isNotEmpty()) println("    ${AMBER}${s.notes}${R}")
                }
                println()
                println(RULE)
                println()

                // Recordes pessoais
                println("  ${SLATE}${ITALIC}recordes pessoais${R}")
                println()
                vm.personalRecords.forEach { pr ->
                    val w = vm.formatWeight(pr.weightKg)
                    println("  ${GOLD}${BOLD}PR${R}  ${CHALK}${pr.exerciseName}${R}  ${STEEL}$w × ${pr.reps} reps${R}  ${SLATE}${pr.date}${R}")
                }
                println()
                println(RULE)
                nav("E exercícios  ·  P planos  ·  U perfil  ·  S sair")
            }

            // ══════════════════════════════════════════════════════
            Screen.EXERCISES -> {
                println()
                println("${CHALK}${BOLD}EXERCÍCIOS${R}")
                println()

                // Filtros ativos
                if (vm.selectedMuscleGroup != null)
                    println("  ${AMBER}grupo: ${vm.selectedMuscleGroup?.name}${R}  ${SLATE}· G0 para limpar${R}")
                if (vm.searchQuery.isNotEmpty())
                    println("  ${AMBER}busca: \"${vm.searchQuery}\"${R}  ${SLATE}· B para limpar${R}")
                println()

                // Grupos musculares
                println("  ${SLATE}${ITALIC}grupo muscular${R}")
                print("  ${STEEL}[G0] todos${R}")
                vm.muscleGroups.forEach { mg ->
                    val active = vm.selectedMuscleGroup?.id == mg.id
                    val style  = if (active) "${LIME}${BOLD}" else STEEL
                    print("   ${style}[G${mg.id.drop(2)}] ${mg.name}${R}")
                }
                println("\n")
                println(RULE)
                println()

                // Lista de exercícios
                if (vm.filteredExercises.isEmpty()) {
                    println("  ${SLATE}nenhum exercício encontrado.${R}")
                } else {
                    vm.filteredExercises.forEachIndexed { i, ex ->
                        val n       = i + 1
                        val diff    = when (ex.difficulty) {
                            1    -> "${LIME}●${R}"
                            2    -> "${AMBER}●${R}"
                            else -> "${CRIMSON}●${R}"
                        }
                        val mgName  = vm.getMuscleGroupName(ex.muscleGroupId)
                        println("  $diff  ${CHALK}${BOLD}[E$n] ${ex.name}${R}  ${SLATE}$mgName  ·  ${ex.sets}×${ex.reps}  ·  ${ex.restSeconds}s descanso${R}")
                    }
                }
                println()
                println(RULE)
                nav("E<n> detalhes  ·  G<n> grupo  ·  B buscar  ·  V voltar")
            }

            // ══════════════════════════════════════════════════════
            Screen.EXERCISE_DETAIL -> {
                val ex = vm.selectedExercise ?: run { screen = Screen.EXERCISES; continue }
                val mgName = vm.getMuscleGroupName(ex.muscleGroupId)
                val diffLabel = vm.getDifficultyLabel(ex.difficulty)
                val diffColor = when (ex.difficulty) { 1 -> LIME; 2 -> AMBER; else -> CRIMSON }

                println()
                println("${CHALK}${BOLD}${ex.name.uppercase()}${R}")
                println("${SLATE}$mgName  ·  $diffColor$diffLabel${R}${SLATE}  ·  ${ex.sets} séries  ·  ${ex.reps} reps  ·  ${ex.restSeconds}s descanso${R}")
                println()
                println(HEAVY)
                println()
                println("  ${STEEL}${ex.description}${R}")
                println()
                println(RULE)
                println()

                // Estrutura da série
                println("  ${SLATE}${ITALIC}estrutura${R}")
                println()
                for (s in 1..ex.sets) {
                    println("  ${SLATE}Série $s${R}   ${CHALK}${ex.reps} reps${R}   ${SLATE}descanso ${ex.restSeconds}s${R}")
                }
                println()

                // PR desse exercício, se existir
                val pr = vm.personalRecords.find { it.exerciseName == ex.name }
                if (pr != null) {
                    println(RULE)
                    println()
                    println("  ${GOLD}${BOLD}PR  ${vm.formatWeight(pr.weightKg)} × ${pr.reps} reps${R}  ${SLATE}${pr.date}${R}")
                    println()
                }

                println(RULE)
                nav("V voltar")
            }

            // ══════════════════════════════════════════════════════
            Screen.PLANS -> {
                println()
                println("${CHALK}${BOLD}PLANOS DE TREINO${R}")
                println()
                println(HEAVY)
                println()

                vm.plans.forEachIndexed { i, plan ->
                    val n      = i + 1
                    val active = vm.activePlan?.id == plan.id
                    val marker = if (active) "${LIME}▶ ${R}" else "  "
                    println("$marker${CHALK}${BOLD}[P$n] ${plan.name}${R}")
                    println("    ${STEEL}${plan.goal}  ·  ${plan.daysPerWeek}×/semana  ·  ${plan.durationWeeks} semanas${R}")
                    println("    ${SLATE}${plan.exerciseIds.size} exercícios no ciclo${R}")
                    println()
                }

                println(RULE)
                nav("P<n> ativar plano  ·  V voltar")
            }

            // ══════════════════════════════════════════════════════
            Screen.ACTIVE_SESSION -> {
                val plan = vm.activePlan
                println()
                println("${LIME}${BOLD}TREINO EM ANDAMENTO${R}")
                if (plan != null) println("${SLATE}${plan.name}${R}")
                println()
                println(HEAVY)
                println()

                // Séries já registradas
                if (vm.currentSessionLogs.isEmpty()) {
                    println("  ${SLATE}nenhuma série registrada ainda.${R}")
                } else {
                    println("  ${SLATE}${ITALIC}séries registradas${R}")
                    println()
                    vm.currentSessionLogs.forEachIndexed { i, log ->
                        val exName = vm.selectedExercise?.name ?: "Exercício"
                        val w      = vm.formatWeight(log.weightKg)
                        println("  ${SLATE}#${i + 1}${R}  ${CHALK}${log.reps} reps  ·  $w${R}")
                    }
                    println()
                }

                println(RULE)
                println()
                println("  ${SLATE}${ITALIC}registrar série${R}")
                println("  [R <reps> <peso>]  ex: R 10 80  (0 para peso corporal)")
                println()
                println("  [F <minutos>]  finalizar sessão  ex: F 55")
                println()
                println(RULE)
                nav("R<reps> <kg> registrar  ·  F<min> finalizar  ·  V cancelar")
            }

            // ══════════════════════════════════════════════════════
            Screen.PROFILE -> {
                val p   = vm.profile
                val imc = "%.1f".format(vm.imc())

                println()
                println("${CHALK}${BOLD}${p.name.uppercase()}${R}")
                println("${SLATE}${p.age} anos  ·  ${p.weightKg} kg  ·  ${p.heightCm} cm  ·  IMC $imc${R}")
                println()
                println(HEAVY)
                println()
                println("  ${SLATE}${ITALIC}objetivo atual${R}")
                println("  ${CHALK}${BOLD}${p.goal}${R}")
                println()
                println(RULE)
                println()

                // Estatísticas gerais
                println("  ${SLATE}${ITALIC}estatísticas${R}")
                println()
                println("  ${STEEL}treinos registrados${R}  ${CHALK}${BOLD}${vm.sessionHistory.size}${R}")
                val totalSets = vm.sessionHistory.sumOf { it.totalSets }
                println("  ${STEEL}séries totais${R}       ${CHALK}${BOLD}$totalSets${R}")
                val avgDur = if (vm.sessionHistory.isEmpty()) 0
                             else vm.sessionHistory.sumOf { it.durationMinutes } / vm.sessionHistory.size
                println("  ${STEEL}duração média${R}       ${CHALK}${BOLD}$avgDur min${R}")
                println()
                println(RULE)
                println()

                println("  ${SLATE}${ITALIC}atualizar${R}")
                println("  [W <kg>]   registrar novo peso  ex: W 83.5")
                println("  [G <texto>] mudar objetivo      ex: G Perder gordura")
                println()
                println(RULE)
                nav("W<kg> peso  ·  G<texto> objetivo  ·  V voltar")
            }
        }

        print("\n  ${SLATE}›${R}  ")
        val input = readlnOrNull()?.trim() ?: ""

        if (input.equals("s", ignoreCase = true) && screen == Screen.DASHBOARD) {
            println()
            println("  ${SLATE}feche o app. descanse. volte amanhã.${R}")
            println()
            break
        }

        try {
            when (screen) {

                Screen.DASHBOARD -> when (input.lowercase()) {
                    "e" -> screen = Screen.EXERCISES
                    "p" -> screen = Screen.PLANS
                    "u" -> screen = Screen.PROFILE
                    "t" -> {
                        if (vm.activePlan != null) screen = Screen.ACTIVE_SESSION
                        else { fb = "ative um plano antes de treinar."; fbOk = false }
                    }
                    "x" -> { vm.clearPlan(); fb = "plano removido."; fbOk = true }
                    else -> { fb = "comando não reconhecido."; fbOk = false }
                }

                Screen.EXERCISES -> {
                    val low = input.lowercase()
                    when {
                        low == "v" -> screen = Screen.DASHBOARD
                        low == "b" -> {
                            if (vm.searchQuery.isNotEmpty()) {
                                vm.setSearchQuery(""); fb = "busca limpa."; fbOk = true
                            } else {
                                print("  buscar: ")
                                val q = readlnOrNull()?.trim() ?: ""
                                vm.setSearchQuery(q); fb = "filtro aplicado."; fbOk = true
                            }
                        }
                        low.startsWith("g") -> {
                            val gid = low.drop(1)
                            if (gid == "0") {
                                vm.selectMuscleGroup(null); fb = "filtro removido."; fbOk = true
                            } else {
                                val mg = vm.muscleGroups.find { it.id == "mg$gid" }
                                if (mg != null) { vm.selectMuscleGroup(mg); fb = "grupo: ${mg.name}"; fbOk = true }
                                else { fb = "grupo inválido."; fbOk = false }
                            }
                        }
                        low.startsWith("e") -> {
                            val idx = low.drop(1).toIntOrNull()
                            if (idx != null && idx in 1..vm.filteredExercises.size) {
                                vm.selectExercise(vm.filteredExercises[idx - 1])
                                screen = Screen.EXERCISE_DETAIL
                            } else { fb = "número inválido."; fbOk = false }
                        }
                        else -> { fb = "use E<n>, G<n>, B ou V."; fbOk = false }
                    }
                }

                Screen.EXERCISE_DETAIL -> when (input.lowercase()) {
                    "v" -> screen = Screen.EXERCISES
                    else -> { fb = "use V para voltar."; fbOk = false }
                }

                Screen.PLANS -> {
                    val low = input.lowercase()
                    when {
                        low == "v" -> screen = Screen.DASHBOARD
                        low.startsWith("p") -> {
                            val idx = low.drop(1).toIntOrNull()
                            if (idx != null && idx in 1..vm.plans.size) {
                                vm.selectPlan(vm.plans[idx - 1])
                                fb = "plano ativado: ${vm.activePlan?.name}"; fbOk = true
                                screen = Screen.DASHBOARD
                            } else { fb = "número inválido."; fbOk = false }
                        }
                        else -> { fb = "use P<n> ou V."; fbOk = false }
                    }
                }

                Screen.ACTIVE_SESSION -> {
                    val low = input.lowercase()
                    when {
                        low == "v" -> {
                            vm.currentSessionLogs.clear()
                            fb = "sessão cancelada."; fbOk = false
                            screen = Screen.DASHBOARD
                        }
                        low.startsWith("r") -> {
                            val parts = low.drop(1).trim().split("\\s+".toRegex())
                            val reps  = parts.getOrNull(0)?.toIntOrNull()
                            val kg    = parts.getOrNull(1)?.toDoubleOrNull() ?: 0.0
                            if (reps != null && reps > 0) {
                                loading("registrando série")
                                vm.logSet(vm.selectedExercise?.id ?: "e1", reps, kg)
                                fb = "série registrada: ${reps} reps · ${vm.formatWeight(kg)}"; fbOk = true
                            } else { fb = "use R <reps> <kg>  ex: R 10 80"; fbOk = false }
                        }
                        low.startsWith("f") -> {
                            val min = low.drop(1).trim().toIntOrNull() ?: 45
                            loading("finalizando sessão")
                            val session = vm.finishSession(min)
                            fb = "sessão salva · ${session.totalSets} séries · ${session.durationMinutes} min"; fbOk = true
                            screen = Screen.DASHBOARD
                        }
                        else -> { fb = "use R <reps> <kg>, F <min> ou V."; fbOk = false }
                    }
                }

                Screen.PROFILE -> {
                    val low = input.lowercase()
                    when {
                        low == "v" -> screen = Screen.DASHBOARD
                        low.startsWith("w") -> {
                            val kg = low.drop(1).trim().toDoubleOrNull()
                            if (kg != null && kg > 0) {
                                vm.updateWeight(kg); fb = "peso atualizado: $kg kg"; fbOk = true
                            } else { fb = "use W <kg>  ex: W 83.5"; fbOk = false }
                        }
                        low.startsWith("g") -> {
                            val goal = input.drop(1).trim()
                            if (goal.isNotEmpty()) {
                                vm.updateGoal(goal); fb = "objetivo: $goal"; fbOk = true
                            } else { fb = "informe o objetivo após G."; fbOk = false }
                        }
                        else -> { fb = "use W<kg>, G<texto> ou V."; fbOk = false }
                    }
                }
            }
        } catch (e: Exception) {
            fb = "erro: ${e.message}"; fbOk = false
        }
    }
}

fun nav(hint: String) = println("  ${SLATE}$hint${R}\n")

fun cls() {
    print("\u001b[H\u001b[2J")
    System.out.flush()
}

fun loading(msg: String) {
    print("  $msg ")
    repeat(3) { Thread.sleep(220); print(".") }
    println()
}