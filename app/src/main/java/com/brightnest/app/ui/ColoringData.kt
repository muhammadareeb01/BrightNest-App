package com.brightnest.app.ui

data class ColorPrim(val type: String, val attrs: Map<String, String>)
data class ColorRegion(
    val id: String,
    val left: Int, val top: Int, val width: Int, val height: Int,
    val prims: List<ColorPrim>
)
data class ColorPicture(
    val id: String, val name: String, val category: String,
    val regions: List<ColorRegion>
)

object ColoringData {
    val palette = listOf(
        "#FFFFFF", "#000000", "#EF4444", "#F97316", "#F59E0B", "#EAB308",
        "#84CC16", "#22C55E", "#10B981", "#06B6D4", "#3B82F6", "#6366F1",
        "#8B5CF6", "#A855F7", "#EC4899", "#F43F5E", "#A3A3A3", "#78350F",
        "#FDE047", "#67E8F9", "#F472B6", "#FCD34D", "#D97706", "#475569"
    )
    val categories = listOf("All", "Animals", "Nature", "Shapes", "Vehicles", "Food", "Objects", "Islamic")

    val pictures: List<ColorPicture> = listOf(
        // ==========================================
        // ANIMALS
        // ==========================================
        ColorPicture("cat", "Cat", "Animals", listOf(
            ColorRegion("tail", 185, 110, 80, 130, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 110 C 50 110 70 70 65 30 C 60 5 35 5 35 25 C 35 55 45 85 10 90 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("body", 70, 140, 160, 130, listOf(
                ColorPrim("Path", mapOf("d" to "M 20 50 C 5 95 15 125 50 125 L 110 125 C 145 125 155 95 140 50 C 130 15 30 15 20 50 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("belly", 110, 160, 80, 100, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 20 C 10 0 70 0 70 20 C 70 75 10 75 10 20 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("paw-l", 95, 235, 40, 35, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 10 C 5 30 35 30 35 10 C 35 0 5 0 5 10 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Line", mapOf("x1" to "15", "y1" to "15", "x2" to "15", "y2" to "28", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Line", mapOf("x1" to "25", "y1" to "15", "x2" to "25", "y2" to "28", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("paw-r", 165, 235, 40, 35, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 10 C 5 30 35 30 35 10 C 35 0 5 0 5 10 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Line", mapOf("x1" to "15", "y1" to "15", "x2" to "15", "y2" to "28", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Line", mapOf("x1" to "25", "y1" to "15", "x2" to "25", "y2" to "28", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("collar", 95, 140, 110, 30, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 5 Q 55 25 105 5 L 102 18 Q 55 35 8 18 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("bell", 140, 155, 20, 20, listOf(
                ColorPrim("Circle", mapOf("cx" to "10", "cy" to "10", "r" to "9", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Circle", mapOf("cx" to "10", "cy" to "12", "r" to "2.5", "fill" to "#111")),
            )),
            ColorRegion("ear-l", 45, 25, 65, 75, listOf(
                ColorPrim("Path", mapOf("d" to "M 20 70 L 5 10 L 60 30 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("ear-in-l", 52, 35, 45, 55, listOf(
                ColorPrim("Path", mapOf("d" to "M 15 50 L 5 10 L 40 22 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("ear-r", 190, 25, 65, 75, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 30 L 60 10 L 45 70 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("ear-in-r", 203, 35, 45, 55, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 22 L 40 10 L 30 50 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("head", 55, 50, 190, 110, listOf(
                ColorPrim("Path", mapOf("d" to "M 15 45 C 5 80 20 105 55 105 L 135 105 C 170 105 185 80 175 45 C 165 10 25 10 15 45 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("cheeks", 100, 95, 100, 55, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 20 C 10 5 90 5 90 20 C 90 50 10 50 10 20 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Line", mapOf("x1" to "5", "y1" to "22", "x2" to "-25", "y2" to "18", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Line", mapOf("x1" to "5", "y1" to "30", "x2" to "-25", "y2" to "32", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Line", mapOf("x1" to "95", "y1" to "22", "x2" to "125", "y2" to "18", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Line", mapOf("x1" to "95", "y1" to "30", "x2" to "125", "y2" to "32", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("eye-l", 85, 75, 36, 36, listOf(
                ColorPrim("Circle", mapOf("cx" to "18", "cy" to "18", "r" to "16", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Circle", mapOf("cx" to "18", "cy" to "18", "r" to "11", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "14", "cy" to "14", "r" to "4", "fill" to "#FFF")),
            )),
            ColorRegion("eye-r", 179, 75, 36, 36, listOf(
                ColorPrim("Circle", mapOf("cx" to "18", "cy" to "18", "r" to "16", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Circle", mapOf("cx" to "18", "cy" to "18", "r" to "11", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "14", "cy" to "14", "r" to "4", "fill" to "#FFF")),
            )),
            ColorRegion("nose", 140, 100, 20, 35, listOf(
                ColorPrim("Polygon", mapOf("points" to "10,12 18,2 2,2", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2")),
                ColorPrim("Path", mapOf("d" to "M 10 12 L 10 20 Q 3 26 0 20 M 10 20 Q 17 26 20 20", "fill" to "none", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
        )),

        ColorPicture("dog", "Dog", "Animals", listOf(
            ColorRegion("tail", 35, 160, 65, 75, listOf(
                ColorPrim("Path", mapOf("d" to "M 55 65 C 25 65 5 45 10 20 C 15 5 32 5 40 20 C 48 35 52 50 55 65 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("body", 65, 145, 170, 125, listOf(
                ColorPrim("Path", mapOf("d" to "M 25 15 C 10 55 10 110 45 115 L 125 115 C 160 110 160 55 145 15 C 120 25 50 25 25 15 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("belly", 105, 155, 90, 105, listOf(
                ColorPrim("Path", mapOf("d" to "M 15 5 C 15 -5 75 -5 75 5 C 75 90 15 90 15 5 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("paw-l", 95, 235, 42, 35, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 10 C 5 30 37 30 37 10 C 37 0 5 0 5 10 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Line", mapOf("x1" to "15", "y1" to "15", "x2" to "15", "y2" to "28", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Line", mapOf("x1" to "27", "y1" to "15", "x2" to "27", "y2" to "28", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("paw-r", 163, 235, 42, 35, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 10 C 5 30 37 30 37 10 C 37 0 5 0 5 10 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Line", mapOf("x1" to "15", "y1" to "15", "x2" to "15", "y2" to "28", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Line", mapOf("x1" to "27", "y1" to "15", "x2" to "27", "y2" to "28", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("collar", 90, 140, 120, 28, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 5 Q 60 25 110 5 L 107 18 Q 60 35 13 18 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("tag", 140, 155, 20, 22, listOf(
                ColorPrim("Circle", mapOf("cx" to "10", "cy" to "10", "r" to "9", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Polygon", mapOf("points" to "10,4 12,8 16,8 13,11 14,15 10,12 6,15 7,11 4,8 8,8", "fill" to "#F59E0B")),
            )),
            ColorRegion("ear-l", 35, 45, 55, 95, listOf(
                ColorPrim("Path", mapOf("d" to "M 45 10 C 15 15 5 55 15 80 C 25 95 45 90 48 60 C 50 40 48 20 45 10 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("ear-r", 210, 45, 55, 95, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 10 C 40 15 50 55 40 80 C 30 95 10 90 7 60 C 5 40 7 20 10 10 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("head", 65, 35, 170, 120, listOf(
                ColorPrim("Path", mapOf("d" to "M 20 45 C 5 85 20 115 55 118 L 115 118 C 150 115 165 85 150 45 C 140 5 30 5 20 45 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("eye-patch", 82, 60, 46, 46, listOf(
                ColorPrim("Circle", mapOf("cx" to "23", "cy" to "23", "r" to "21", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("eye-l", 92, 70, 28, 28, listOf(
                ColorPrim("Circle", mapOf("cx" to "14", "cy" to "14", "r" to "13", "fill" to "#111", "stroke" to "#111", "strokeWidth" to "2")),
                ColorPrim("Circle", mapOf("cx" to "10", "cy" to "10", "r" to "4.5", "fill" to "#FFF")),
                ColorPrim("Circle", mapOf("cx" to "17", "cy" to "17", "r" to "2", "fill" to "#FFF")),
            )),
            ColorRegion("eye-r", 180, 70, 28, 28, listOf(
                ColorPrim("Circle", mapOf("cx" to "14", "cy" to "14", "r" to "13", "fill" to "#111", "stroke" to "#111", "strokeWidth" to "2")),
                ColorPrim("Circle", mapOf("cx" to "10", "cy" to "10", "r" to "4.5", "fill" to "#FFF")),
                ColorPrim("Circle", mapOf("cx" to "17", "cy" to "17", "r" to "2", "fill" to "#FFF")),
            )),
            ColorRegion("muzzle", 110, 90, 80, 55, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 18 C 10 2 70 2 70 18 C 70 48 10 48 10 18 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("tongue", 142, 122, 16, 18, listOf(
                ColorPrim("Path", mapOf("d" to "M 1 1 Q 8 18 15 1 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("nose", 135, 95, 30, 30, listOf(
                ColorPrim("Path", mapOf("d" to "M 15 2 C 26 2 28 14 15 16 C 2 14 4 2 15 2 Z", "fill" to "#111", "stroke" to "#111", "strokeWidth" to "2")),
                ColorPrim("Circle", mapOf("cx" to "11", "cy" to "6", "r" to "2.5", "fill" to "#FFF")),
                ColorPrim("Path", mapOf("d" to "M 15 16 L 15 22 Q 8 28 2 22 M 15 22 Q 22 28 28 22", "fill" to "none", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
        )),

        ColorPicture("bird", "Bird", "Animals", listOf(
            ColorRegion("branch", 20, 215, 260, 45, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 20 Q 130 10 255 25 L 255 35 Q 130 20 5 30 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("leaf-1", 45, 195, 45, 30, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 25 Q 20 0 40 5 Q 25 30 5 25 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("leaf-2", 215, 220, 45, 30, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 5 Q 35 5 40 25 Q 15 30 5 5 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("tail", 20, 135, 75, 75, listOf(
                ColorPrim("Path", mapOf("d" to "M 70 15 L 10 50 L 5 70 L 40 60 L 70 35 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("body", 75, 70, 165, 155, listOf(
                ColorPrim("Path", mapOf("d" to "M 30 30 C 5 75 10 135 70 145 C 130 155 160 115 155 70 C 150 15 65 5 30 30 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("belly", 120, 115, 100, 100, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 10 C 65 10 90 45 85 80 C 50 95 10 85 5 55 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("wing", 60, 95, 95, 90, listOf(
                ColorPrim("Path", mapOf("d" to "M 35 10 C 80 15 90 55 80 80 C 60 85 20 80 10 50 C 5 25 15 10 35 10 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Path", mapOf("d" to "M 30 35 Q 60 40 65 65 M 20 55 Q 50 60 50 78", "fill" to "none", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("head", 135, 30, 85, 85, listOf(
                ColorPrim("Circle", mapOf("cx" to "42", "cy" to "42", "r" to "38", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("crest", 145, 10, 45, 35, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 30 Q 15 5 35 10 Q 25 30 15 32 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("eye", 175, 50, 30, 30, listOf(
                ColorPrim("Circle", mapOf("cx" to "15", "cy" to "15", "r" to "13", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Circle", mapOf("cx" to "15", "cy" to "15", "r" to "8", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "12", "cy" to "12", "r" to "3", "fill" to "#FFF")),
            )),
            ColorRegion("beak", 205, 60, 45, 30, listOf(
                ColorPrim("Polygon", mapOf("points" to "2,5 40,15 2,25", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
        )),

        ColorPicture("butterfly", "Butterfly", "Animals", listOf(
            ColorRegion("wing-tl", 25, 35, 125, 125, listOf(
                ColorPrim("Path", mapOf("d" to "M 120 115 C 30 110 5 70 10 25 C 20 -5 90 5 110 45 C 118 60 120 90 120 115 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("spot-tl", 55, 55, 50, 50, listOf(
                ColorPrim("Circle", mapOf("cx" to "25", "cy" to "25", "r" to "22", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Circle", mapOf("cx" to "25", "cy" to "25", "r" to "12", "fill" to "#FFF", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("wing-tr", 150, 35, 125, 125, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 115 C 95 110 120 70 115 25 C 105 -5 35 5 15 45 C 7 60 5 90 5 115 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("spot-tr", 195, 55, 50, 50, listOf(
                ColorPrim("Circle", mapOf("cx" to "25", "cy" to "25", "r" to "22", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Circle", mapOf("cx" to "25", "cy" to "25", "r" to "12", "fill" to "#FFF", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("wing-bl", 40, 145, 110, 115, listOf(
                ColorPrim("Path", mapOf("d" to "M 105 10 C 40 25 15 70 25 100 C 40 115 90 100 100 65 C 105 45 105 20 105 10 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("spot-bl", 65, 175, 40, 40, listOf(
                ColorPrim("Circle", mapOf("cx" to "20", "cy" to "20", "r" to "16", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("wing-br", 150, 145, 110, 115, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 10 C 70 25 95 70 85 100 C 70 115 20 100 10 65 C 5 45 5 20 5 10 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("spot-br", 195, 175, 40, 40, listOf(
                ColorPrim("Circle", mapOf("cx" to "20", "cy" to "20", "r" to "16", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("body", 138, 70, 24, 170, listOf(
                ColorPrim("Path", mapOf("d" to "M 12 5 C 22 5 22 155 12 165 C 2 155 2 5 12 5 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
                ColorPrim("Line", mapOf("x1" to "3", "y1" to "40", "x2" to "21", "y2" to "40", "stroke" to "#111", "strokeWidth" to "2")),
                ColorPrim("Line", mapOf("x1" to "3", "y1" to "70", "x2" to "21", "y2" to "70", "stroke" to "#111", "strokeWidth" to "2")),
                ColorPrim("Line", mapOf("x1" to "3", "y1" to "100", "x2" to "21", "y2" to "100", "stroke" to "#111", "strokeWidth" to "2")),
                ColorPrim("Line", mapOf("x1" to "4", "y1" to "130", "x2" to "20", "y2" to "130", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("head", 135, 45, 30, 30, listOf(
                ColorPrim("Circle", mapOf("cx" to "15", "cy" to "15", "r" to "13", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Path", mapOf("d" to "M 10 5 C 0 -15 -10 -20 -15 -18 M 20 5 C 30 -15 40 -20 45 -18", "fill" to "none", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Circle", mapOf("cx" to "-15", "cy" to "-18", "r" to "3.5", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "45", "cy" to "-18", "r" to "3.5", "fill" to "#111")),
            )),
        )),

        ColorPicture("fish", "Fish", "Animals", listOf(
            ColorRegion("tail", 15, 80, 85, 140, listOf(
                ColorPrim("Path", mapOf("d" to "M 80 70 L 10 10 C 25 50 25 90 10 130 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
                ColorPrim("Path", mapOf("d" to "M 70 70 Q 30 40 20 30 M 70 70 Q 30 100 20 110", "fill" to "none", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("fin-top", 120, 30, 90, 60, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 55 Q 45 5 85 45 C 55 45 25 50 5 55 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("fin-bot", 130, 205, 75, 55, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 5 Q 40 50 70 15 C 45 15 25 10 5 5 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("body", 85, 70, 185, 155, listOf(
                ColorPrim("Path", mapOf("d" to "M 15 80 C 15 15 130 10 180 80 C 130 150 15 145 15 80 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("stripe-1", 120, 80, 35, 135, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 15 Q 30 70 5 125 L 30 120 Q 55 70 30 10 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("stripe-2", 175, 80, 35, 135, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 5 Q 30 70 5 130 L 28 122 Q 50 70 28 12 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("fin-side", 145, 130, 55, 45, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 10 Q 50 5 45 40 Q 20 40 5 10 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("eye", 215, 110, 35, 35, listOf(
                ColorPrim("Circle", mapOf("cx" to "17", "cy" to "17", "r" to "15", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Circle", mapOf("cx" to "17", "cy" to "17", "r" to "9", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "14", "cy" to "14", "r" to "3.5", "fill" to "#FFF")),
            )),
            ColorRegion("bubble-1", 260, 60, 25, 25, listOf(
                ColorPrim("Circle", mapOf("cx" to "12", "cy" to "12", "r" to "10", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("bubble-2", 270, 30, 18, 18, listOf(
                ColorPrim("Circle", mapOf("cx" to "9", "cy" to "9", "r" to "7", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2")),
            )),
        )),

        ColorPicture("turtle", "Turtle", "Animals", listOf(
            ColorRegion("flipper-bl", 45, 165, 55, 55, listOf(
                ColorPrim("Path", mapOf("d" to "M 45 10 C 25 20 5 45 15 50 C 30 55 50 35 50 15 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("flipper-br", 200, 165, 55, 55, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 10 C 30 20 50 45 40 50 C 25 55 5 35 5 15 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("tail", 140, 220, 20, 40, listOf(
                ColorPrim("Polygon", mapOf("points" to "2,5 18,5 10,35", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("flipper-fl", 25, 75, 75, 75, listOf(
                ColorPrim("Path", mapOf("d" to "M 70 45 C 50 15 10 10 5 35 C 0 60 40 70 65 55 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("flipper-fr", 200, 75, 75, 75, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 45 C 25 15 65 10 70 35 C 75 60 35 70 10 55 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("head", 115, 20, 70, 75, listOf(
                ColorPrim("Path", mapOf("d" to "M 15 65 C 5 40 15 5 35 5 C 55 5 65 40 55 65 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
                ColorPrim("Circle", mapOf("cx" to "25", "cy" to "30", "r" to "4", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "45", "cy" to "30", "r" to "4", "fill" to "#111")),
                ColorPrim("Path", mapOf("d" to "M 28 48 Q 35 55 42 48", "fill" to "none", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("shell", 60, 75, 180, 155, listOf(
                ColorPrim("Path", mapOf("d" to "M 15 75 C 15 15 165 15 165 75 C 165 140 15 140 15 75 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "4")),
            )),
            ColorRegion("plate-mid", 120, 120, 60, 60, listOf(
                ColorPrim("Polygon", mapOf("points" to "30,5 55,20 55,45 30,55 5,45 5,20", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("plate-top", 120, 85, 60, 40, listOf(
                ColorPrim("Polygon", mapOf("points" to "10,35 50,35 40,5 20,5", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("plate-bot", 120, 175, 60, 40, listOf(
                ColorPrim("Polygon", mapOf("points" to "10,5 50,5 40,35 20,35", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("plate-l", 78, 120, 45, 60, listOf(
                ColorPrim("Polygon", mapOf("points" to "42,15 42,45 8,40 5,20", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("plate-r", 177, 120, 45, 60, listOf(
                ColorPrim("Polygon", mapOf("points" to "3,15 3,45 37,40 40,20", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
        )),

        ColorPicture("crab", "Crab", "Animals", listOf(
            ColorRegion("arm-l", 35, 75, 70, 65, listOf(
                ColorPrim("Path", mapOf("d" to "M 65 55 C 35 55 15 35 20 10 L 35 15 C 30 35 45 45 65 45 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("claw-l", 15, 30, 75, 70, listOf(
                ColorPrim("Path", mapOf("d" to "M 45 65 C 20 60 5 40 10 15 C 25 15 40 30 45 40 C 50 30 65 15 70 25 C 75 45 65 60 45 65 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("arm-r", 195, 75, 70, 65, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 55 C 35 55 55 35 50 10 L 35 15 C 40 35 25 45 5 45 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("claw-r", 210, 30, 75, 70, listOf(
                ColorPrim("Path", mapOf("d" to "M 30 65 C 55 60 70 40 65 15 C 50 15 35 30 30 40 C 25 30 10 15 5 25 C 0 45 10 60 30 65 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("leg-l1", 30, 140, 55, 45, listOf(
                ColorPrim("Path", mapOf("d" to "M 50 10 Q 20 5 5 35", "fill" to "none", "stroke" to "#111", "strokeWidth" to "4")),
            )),
            ColorRegion("leg-l2", 30, 175, 55, 45, listOf(
                ColorPrim("Path", mapOf("d" to "M 50 10 Q 15 15 5 40", "fill" to "none", "stroke" to "#111", "strokeWidth" to "4")),
            )),
            ColorRegion("leg-r1", 215, 140, 55, 45, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 10 Q 35 5 50 35", "fill" to "none", "stroke" to "#111", "strokeWidth" to "4")),
            )),
            ColorRegion("leg-r2", 215, 175, 55, 45, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 10 Q 40 15 50 40", "fill" to "none", "stroke" to "#111", "strokeWidth" to "4")),
            )),
            ColorRegion("body", 65, 115, 170, 110, listOf(
                ColorPrim("Path", mapOf("d" to "M 15 55 C 15 15 155 15 155 55 C 155 95 15 95 15 55 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "4")),
                ColorPrim("Path", mapOf("d" to "M 55 65 Q 85 85 115 65", "fill" to "none", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Circle", mapOf("cx" to "45", "cy" to "58", "r" to "6", "fill" to "#F472B6")),
                ColorPrim("Circle", mapOf("cx" to "125", "cy" to "58", "r" to "6", "fill" to "#F472B6")),
            )),
            ColorRegion("eye-l", 100, 70, 35, 50, listOf(
                ColorPrim("Path", mapOf("d" to "M 17 48 L 17 25", "fill" to "none", "stroke" to "#111", "strokeWidth" to "3.5")),
                ColorPrim("Circle", mapOf("cx" to "17", "cy" to "17", "r" to "15", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Circle", mapOf("cx" to "17", "cy" to "17", "r" to "8", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "14", "cy" to "14", "r" to "3", "fill" to "#FFF")),
            )),
            ColorRegion("eye-r", 165, 70, 35, 50, listOf(
                ColorPrim("Path", mapOf("d" to "M 17 48 L 17 25", "fill" to "none", "stroke" to "#111", "strokeWidth" to "3.5")),
                ColorPrim("Circle", mapOf("cx" to "17", "cy" to "17", "r" to "15", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Circle", mapOf("cx" to "17", "cy" to "17", "r" to "8", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "14", "cy" to "14", "r" to "3", "fill" to "#FFF")),
            )),
        )),

        ColorPicture("bee", "Bee", "Animals", listOf(
            ColorRegion("wing-l", 55, 30, 90, 85, listOf(
                ColorPrim("Path", mapOf("d" to "M 80 80 C 40 80 5 50 15 20 C 25 -5 70 15 80 80 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Path", mapOf("d" to "M 35 25 Q 60 50 75 75", "fill" to "none", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("wing-r", 155, 30, 90, 85, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 80 C 50 80 85 50 75 20 C 65 -5 20 15 10 80 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Path", mapOf("d" to "M 55 25 Q 30 50 15 75", "fill" to "none", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("stinger", 25, 145, 35, 30, listOf(
                ColorPrim("Polygon", mapOf("points" to "30,5 5,15 30,25", "fill" to "#111", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("body", 50, 95, 200, 130, listOf(
                ColorPrim("Path", mapOf("d" to "M 25 65 C 25 15 175 15 175 65 C 175 115 25 115 25 65 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "4")),
            )),
            ColorRegion("stripe-1", 95, 98, 35, 124, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 8 Q 25 60 5 116 L 30 116 Q 50 60 30 8 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("stripe-2", 155, 98, 35, 124, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 8 Q 25 60 5 116 L 30 116 Q 50 60 30 8 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("head", 195, 105, 75, 110, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 10 C 45 10 65 35 65 55 C 65 75 45 100 10 100 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
                ColorPrim("Path", mapOf("d" to "M 30 75 Q 45 85 55 70", "fill" to "none", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Circle", mapOf("cx" to "40", "cy" to "75", "r" to "4", "fill" to "#F472B6")),
            )),
            ColorRegion("eye", 220, 125, 30, 30, listOf(
                ColorPrim("Circle", mapOf("cx" to "15", "cy" to "15", "r" to "13", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Circle", mapOf("cx" to "15", "cy" to "15", "r" to "8", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "12", "cy" to "12", "r" to "3", "fill" to "#FFF")),
            )),
            ColorRegion("antennae", 215, 60, 55, 55, listOf(
                ColorPrim("Path", mapOf("d" to "M 15 50 Q 10 15 35 10 M 25 50 Q 35 20 50 25", "fill" to "none", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Circle", mapOf("cx" to "35", "cy" to "10", "r" to "4.5", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "50", "cy" to "25", "r" to "4.5", "fill" to "#111")),
            )),
        )),

        ColorPicture("ladybug", "Ladybug", "Animals", listOf(
            ColorRegion("leaf", 20, 160, 260, 120, listOf(
                ColorPrim("Path", mapOf("d" to "M 15 80 C 60 10 200 20 250 80 C 180 125 70 120 15 80 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
                ColorPrim("Path", mapOf("d" to "M 15 80 Q 130 75 250 80", "fill" to "none", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("head", 110, 30, 80, 70, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 60 C 10 20 70 20 70 60 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
                ColorPrim("Circle", mapOf("cx" to "25", "cy" to "40", "r" to "5", "fill" to "#FFF")),
                ColorPrim("Circle", mapOf("cx" to "55", "cy" to "40", "r" to "5", "fill" to "#FFF")),
                ColorPrim("Path", mapOf("d" to "M 25 25 Q 15 5 5 10 M 55 25 Q 65 5 75 10", "fill" to "none", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Circle", mapOf("cx" to "5", "cy" to "10", "r" to "3", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "75", "cy" to "10", "r" to "3", "fill" to "#111")),
            )),
            ColorRegion("wing-l", 50, 75, 100, 150, listOf(
                ColorPrim("Path", mapOf("d" to "M 95 10 C 25 10 10 70 15 110 C 20 140 70 145 95 145 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("wing-r", 150, 75, 100, 150, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 10 C 75 10 90 70 85 110 C 80 140 30 145 5 145 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("dot-1", 75, 105, 30, 30, listOf(
                ColorPrim("Circle", mapOf("cx" to "15", "cy" to "15", "r" to "13", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("dot-2", 65, 155, 30, 30, listOf(
                ColorPrim("Circle", mapOf("cx" to "15", "cy" to "15", "r" to "13", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("dot-3", 110, 175, 25, 25, listOf(
                ColorPrim("Circle", mapOf("cx" to "12", "cy" to "12", "r" to "10", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("dot-4", 195, 105, 30, 30, listOf(
                ColorPrim("Circle", mapOf("cx" to "15", "cy" to "15", "r" to "13", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("dot-5", 205, 155, 30, 30, listOf(
                ColorPrim("Circle", mapOf("cx" to "15", "cy" to "15", "r" to "13", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("dot-6", 165, 175, 25, 25, listOf(
                ColorPrim("Circle", mapOf("cx" to "12", "cy" to "12", "r" to "10", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2")),
            )),
        )),

        ColorPicture("owl", "Owl", "Animals", listOf(
            ColorRegion("branch", 20, 235, 260, 40, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 20 Q 130 5 255 20 L 255 35 Q 130 20 5 35 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("body", 60, 45, 180, 205, listOf(
                ColorPrim("Path", mapOf("d" to "M 20 45 C 5 120 20 195 90 195 C 160 195 175 120 160 45 C 150 15 30 15 20 45 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("ear-l", 65, 25, 45, 45, listOf(
                ColorPrim("Polygon", mapOf("points" to "5,40 20,5 40,35", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("ear-r", 190, 25, 45, 45, listOf(
                ColorPrim("Polygon", mapOf("points" to "5,35 25,5 40,40", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("wing-l", 45, 95, 55, 115, listOf(
                ColorPrim("Path", mapOf("d" to "M 45 10 C 15 30 5 80 20 105 C 35 90 50 50 45 10 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("wing-r", 200, 95, 55, 115, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 10 C 40 30 50 80 35 105 C 20 90 5 50 10 10 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("belly", 100, 135, 100, 105, listOf(
                ColorPrim("Path", mapOf("d" to "M 15 15 C 15 0 85 0 85 15 C 85 90 15 90 15 15 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Path", mapOf("d" to "M 35 30 Q 50 40 65 30 M 25 50 Q 50 65 75 50 M 35 70 Q 50 80 65 70", "fill" to "none", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("eye-ring-l", 75, 65, 65, 65, listOf(
                ColorPrim("Circle", mapOf("cx" to "32", "cy" to "32", "r" to "30", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("eye-l", 90, 80, 36, 36, listOf(
                ColorPrim("Circle", mapOf("cx" to "18", "cy" to "18", "r" to "16", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "13", "cy" to "13", "r" to "5", "fill" to "#FFF")),
            )),
            ColorRegion("eye-ring-r", 160, 65, 65, 65, listOf(
                ColorPrim("Circle", mapOf("cx" to "32", "cy" to "32", "r" to "30", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("eye-r", 174, 80, 36, 36, listOf(
                ColorPrim("Circle", mapOf("cx" to "18", "cy" to "18", "r" to "16", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "13", "cy" to "13", "r" to "5", "fill" to "#FFF")),
            )),
            ColorRegion("beak", 140, 105, 20, 30, listOf(
                ColorPrim("Polygon", mapOf("points" to "10,25 18,5 2,5", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
        )),

        ColorPicture("frog", "Frog", "Animals", listOf(
            ColorRegion("lilypad", 20, 205, 260, 75, listOf(
                ColorPrim("Path", mapOf("d" to "M 20 40 C 20 5 240 5 240 40 C 240 70 160 70 130 45 L 120 70 C 50 70 20 60 20 40 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("leg-l", 35, 140, 65, 75, listOf(
                ColorPrim("Path", mapOf("d" to "M 55 10 C 20 15 5 50 20 70 C 40 75 55 50 55 10 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("leg-r", 200, 140, 65, 75, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 10 C 45 15 60 50 45 70 C 25 75 10 50 10 10 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("body", 65, 80, 170, 135, listOf(
                ColorPrim("Path", mapOf("d" to "M 15 65 C 15 15 155 15 155 65 C 155 125 15 125 15 65 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "4")),
                ColorPrim("Path", mapOf("d" to "M 45 75 Q 85 105 125 75", "fill" to "none", "stroke" to "#111", "strokeWidth" to "3.5")),
                ColorPrim("Circle", mapOf("cx" to "35", "cy" to "70", "r" to "7", "fill" to "#F472B6")),
                ColorPrim("Circle", mapOf("cx" to "135", "cy" to "70", "r" to "7", "fill" to "#F472B6")),
            )),
            ColorRegion("belly", 105, 125, 90, 80, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 15 C 10 0 80 0 80 15 C 80 65 10 65 10 15 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("eye-l", 70, 35, 55, 55, listOf(
                ColorPrim("Circle", mapOf("cx" to "27", "cy" to "27", "r" to "25", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
                ColorPrim("Circle", mapOf("cx" to "27", "cy" to "27", "r" to "14", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "22", "cy" to "22", "r" to "5", "fill" to "#FFF")),
            )),
            ColorRegion("eye-r", 175, 35, 55, 55, listOf(
                ColorPrim("Circle", mapOf("cx" to "27", "cy" to "27", "r" to "25", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
                ColorPrim("Circle", mapOf("cx" to "27", "cy" to "27", "r" to "14", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "22", "cy" to "22", "r" to "5", "fill" to "#FFF")),
            )),
        )),

        // ==========================================
        // NATURE
        // ==========================================
        ColorPicture("flower", "Flower", "Nature", listOf(
            ColorRegion("pot", 100, 205, 100, 75, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 5 L 95 5 L 80 70 L 20 70 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
                ColorPrim("Rect", mapOf("x" to "2", "y" to "2", "width" to "96", "height" to "16", "rx" to "4", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("stem", 140, 135, 20, 80, listOf(
                ColorPrim("Rect", mapOf("x" to "5", "y" to "2", "width" to "10", "height" to "76", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("leaf-l", 85, 155, 60, 35, listOf(
                ColorPrim("Path", mapOf("d" to "M 55 25 Q 25 5 5 15 Q 25 35 55 25 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("leaf-r", 155, 140, 60, 35, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 25 Q 35 5 55 15 Q 35 35 5 25 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("petal-1", 115, 20, 70, 70, listOf(
                ColorPrim("Circle", mapOf("cx" to "35", "cy" to "35", "r" to "30", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("petal-2", 175, 45, 70, 70, listOf(
                ColorPrim("Circle", mapOf("cx" to "35", "cy" to "35", "r" to "30", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("petal-3", 175, 105, 70, 70, listOf(
                ColorPrim("Circle", mapOf("cx" to "35", "cy" to "35", "r" to "30", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("petal-4", 115, 130, 70, 70, listOf(
                ColorPrim("Circle", mapOf("cx" to "35", "cy" to "35", "r" to "30", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("petal-5", 55, 105, 70, 70, listOf(
                ColorPrim("Circle", mapOf("cx" to "35", "cy" to "35", "r" to "30", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("petal-6", 55, 45, 70, 70, listOf(
                ColorPrim("Circle", mapOf("cx" to "35", "cy" to "35", "r" to "30", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("center", 110, 70, 80, 80, listOf(
                ColorPrim("Circle", mapOf("cx" to "40", "cy" to "40", "r" to "36", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
                ColorPrim("Circle", mapOf("cx" to "28", "cy" to "35", "r" to "4", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "52", "cy" to "35", "r" to "4", "fill" to "#111")),
                ColorPrim("Path", mapOf("d" to "M 30 48 Q 40 58 50 48", "fill" to "none", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
        )),

        ColorPicture("tree", "Tree", "Nature", listOf(
            ColorRegion("trunk", 120, 160, 60, 115, listOf(
                ColorPrim("Path", mapOf("d" to "M 15 5 L 15 110 L 5 110 L 5 115 L 55 115 L 55 110 L 45 110 L 45 5 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
                ColorPrim("Path", mapOf("d" to "M 25 40 Q 30 70 25 100 M 35 20 Q 40 50 35 80", "fill" to "none", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("leaves-bot", 40, 100, 220, 95, listOf(
                ColorPrim("Path", mapOf("d" to "M 15 65 C 5 35 30 10 65 20 C 85 -5 135 -5 155 20 C 190 10 215 35 205 65 C 215 95 175 115 110 95 C 45 115 5 95 15 65 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("leaves-top", 60, 25, 180, 105, listOf(
                ColorPrim("Path", mapOf("d" to "M 20 70 C 5 40 30 5 70 15 C 90 -10 130 -5 150 25 C 180 30 185 70 160 90 C 120 105 50 105 20 70 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("apple-1", 85, 75, 26, 26, listOf(
                ColorPrim("Circle", mapOf("cx" to "13", "cy" to "13", "r" to "11", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("apple-2", 185, 85, 26, 26, listOf(
                ColorPrim("Circle", mapOf("cx" to "13", "cy" to "13", "r" to "11", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("apple-3", 135, 45, 26, 26, listOf(
                ColorPrim("Circle", mapOf("cx" to "13", "cy" to "13", "r" to "11", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2")),
            )),
        )),

        ColorPicture("sun", "Sun", "Nature", listOf(
            ColorRegion("ray-1", 135, 15, 30, 45, listOf(
                ColorPrim("Polygon", mapOf("points" to "15,2 28,40 2,40", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("ray-2", 215, 45, 40, 40, listOf(
                ColorPrim("Polygon", mapOf("points" to "35,10 15,38 2,15", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("ray-3", 240, 135, 45, 30, listOf(
                ColorPrim("Polygon", mapOf("points" to "40,15 2,28 2,2", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("ray-4", 215, 215, 40, 40, listOf(
                ColorPrim("Polygon", mapOf("points" to "35,30 2,25 25,2", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("ray-5", 135, 240, 30, 45, listOf(
                ColorPrim("Polygon", mapOf("points" to "15,40 2,2 28,2", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("ray-6", 45, 215, 40, 40, listOf(
                ColorPrim("Polygon", mapOf("points" to "5,30 15,2 38,25", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("ray-7", 15, 135, 45, 30, listOf(
                ColorPrim("Polygon", mapOf("points" to "2,15 40,2 40,28", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("ray-8", 45, 45, 40, 40, listOf(
                ColorPrim("Polygon", mapOf("points" to "5,10 38,15 15,38", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("sun-body", 70, 70, 160, 160, listOf(
                ColorPrim("Circle", mapOf("cx" to "80", "cy" to "80", "r" to "75", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "4")),
                ColorPrim("Circle", mapOf("cx" to "55", "cy" to "65", "r" to "7", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "105", "cy" to "65", "r" to "7", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "45", "cy" to "85", "r" to "8", "fill" to "#F472B6")),
                ColorPrim("Circle", mapOf("cx" to "115", "cy" to "85", "r" to "8", "fill" to "#F472B6")),
                ColorPrim("Path", mapOf("d" to "M 55 95 Q 80 120 105 95", "fill" to "none", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
        )),

        ColorPicture("moon", "Moon", "Nature", listOf(
            ColorRegion("cloud", 30, 170, 240, 95, listOf(
                ColorPrim("Path", mapOf("d" to "M 30 75 C 10 75 5 50 25 40 C 15 15 55 5 80 25 C 105 -5 160 -5 180 25 C 205 10 235 30 225 55 C 245 70 230 90 205 85 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("moon", 75, 25, 165, 175, listOf(
                ColorPrim("Path", mapOf("d" to "M 80 5 C 145 5 165 75 145 140 C 125 155 85 165 60 160 C 130 135 125 45 40 40 C 50 20 65 5 80 5 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
                ColorPrim("Path", mapOf("d" to "M 105 75 Q 115 85 125 75", "fill" to "none", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Circle", mapOf("cx" to "115", "cy" to "65", "r" to "3.5", "fill" to "#111")),
            )),
            ColorRegion("star1", 35, 45, 40, 40, listOf(
                ColorPrim("Polygon", mapOf("points" to "20,2 25,14 38,14 28,22 32,35 20,27 8,35 12,22 2,14 15,14", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("star2", 215, 65, 35, 35, listOf(
                ColorPrim("Polygon", mapOf("points" to "17,2 21,12 32,12 24,19 27,30 17,23 7,30 10,19 2,12 13,12", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
        )),

        ColorPicture("cloud", "Cloud", "Nature", listOf(
            ColorRegion("cloud", 35, 55, 230, 130, listOf(
                ColorPrim("Path", mapOf("d" to "M 45 110 C 15 110 5 75 35 60 C 20 25 75 15 105 35 C 135 -5 200 5 205 50 C 235 60 235 100 200 110 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "4")),
                ColorPrim("Circle", mapOf("cx" to "85", "cy" to "70", "r" to "6", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "145", "cy" to "70", "r" to "6", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "70", "cy" to "82", "r" to "7", "fill" to "#F472B6")),
                ColorPrim("Circle", mapOf("cx" to "160", "cy" to "82", "r" to "7", "fill" to "#F472B6")),
                ColorPrim("Path", mapOf("d" to "M 100 85 Q 115 98 130 85", "fill" to "none", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("drop-1", 65, 205, 25, 45, listOf(
                ColorPrim("Path", mapOf("d" to "M 12 5 C 22 25 22 40 12 40 C 2 40 2 25 12 5 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("drop-2", 115, 215, 25, 45, listOf(
                ColorPrim("Path", mapOf("d" to "M 12 5 C 22 25 22 40 12 40 C 2 40 2 25 12 5 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("drop-3", 165, 205, 25, 45, listOf(
                ColorPrim("Path", mapOf("d" to "M 12 5 C 22 25 22 40 12 40 C 2 40 2 25 12 5 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("drop-4", 215, 215, 25, 45, listOf(
                ColorPrim("Path", mapOf("d" to "M 12 5 C 22 25 22 40 12 40 C 2 40 2 25 12 5 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
        )),

        ColorPicture("rainbow", "Rainbow", "Nature", listOf(
            ColorRegion("arc-1", 30, 40, 240, 200, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 180 A 110 110 0 0 1 230 180 L 205 180 A 85 85 0 0 0 35 180 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("arc-2", 55, 65, 190, 160, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 140 A 85 85 0 0 1 180 140 L 155 140 A 60 60 0 0 0 35 140 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("arc-3", 80, 90, 140, 120, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 100 A 60 60 0 0 1 130 100 L 105 100 A 35 35 0 0 0 35 100 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("cloud-l", 15, 175, 105, 75, listOf(
                ColorPrim("Path", mapOf("d" to "M 20 60 C 5 60 5 35 25 30 C 20 10 50 5 65 20 C 85 5 100 25 90 45 C 105 55 95 70 80 65 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("cloud-r", 180, 175, 105, 75, listOf(
                ColorPrim("Path", mapOf("d" to "M 20 60 C 5 60 5 35 25 30 C 20 10 50 5 65 20 C 85 5 100 25 90 45 C 105 55 95 70 80 65 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
        )),

        // ==========================================
        // VEHICLES
        // ==========================================
        ColorPicture("car", "Car", "Vehicles", listOf(
            ColorRegion("cabin", 70, 75, 160, 65, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 60 L 35 10 L 125 10 L 150 60 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("window-f", 145, 85, 70, 50, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 45 L 5 5 L 45 5 L 65 45 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("window-b", 85, 85, 55, 50, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 45 L 25 5 L 50 5 L 50 45 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("body", 20, 130, 260, 75, listOf(
                ColorPrim("Path", mapOf("d" to "M 15 25 C 20 5 50 5 65 5 L 205 5 C 235 5 255 15 250 45 C 245 65 235 65 215 65 L 45 65 C 25 65 10 55 15 25 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("headlight", 245, 145, 25, 25, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 5 Q 20 12 5 20 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("taillight", 20, 145, 20, 20, listOf(
                ColorPrim("Rect", mapOf("x" to "2", "y" to "2", "width" to "12", "height" to "16", "rx" to "3", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("wheel-l", 50, 175, 60, 60, listOf(
                ColorPrim("Circle", mapOf("cx" to "30", "cy" to "30", "r" to "28", "fill" to "#111", "stroke" to "#111", "strokeWidth" to "2")),
                ColorPrim("Circle", mapOf("cx" to "30", "cy" to "30", "r" to "14", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Circle", mapOf("cx" to "30", "cy" to "30", "r" to "5", "fill" to "#111")),
            )),
            ColorRegion("wheel-r", 190, 175, 60, 60, listOf(
                ColorPrim("Circle", mapOf("cx" to "30", "cy" to "30", "r" to "28", "fill" to "#111", "stroke" to "#111", "strokeWidth" to "2")),
                ColorPrim("Circle", mapOf("cx" to "30", "cy" to "30", "r" to "14", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Circle", mapOf("cx" to "30", "cy" to "30", "r" to "5", "fill" to "#111")),
            )),
        )),

        ColorPicture("boat", "Boat", "Vehicles", listOf(
            ColorRegion("waves", 15, 225, 270, 50, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 20 Q 35 5 70 20 Q 105 5 140 20 Q 175 5 210 20 Q 245 5 265 20 L 265 40 L 5 40 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("hull", 35, 170, 230, 65, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 10 L 225 10 L 195 55 L 45 55 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
                ColorPrim("Circle", mapOf("cx" to "80", "cy" to "30", "r" to "8", "fill" to "#FFF", "stroke" to "#111", "strokeWidth" to "2")),
                ColorPrim("Circle", mapOf("cx" to "120", "cy" to "30", "r" to "8", "fill" to "#FFF", "stroke" to "#111", "strokeWidth" to "2")),
                ColorPrim("Circle", mapOf("cx" to "160", "cy" to "30", "r" to "8", "fill" to "#FFF", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("mast", 130, 25, 15, 150, listOf(
                ColorPrim("Rect", mapOf("x" to "4", "y" to "2", "width" to "7", "height" to "146", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("sail-main", 140, 35, 105, 130, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 125 L 5 10 Q 75 60 100 125 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("sail-front", 45, 55, 85, 110, listOf(
                ColorPrim("Path", mapOf("d" to "M 80 105 L 80 15 L 10 105 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("flag", 135, 15, 45, 25, listOf(
                ColorPrim("Polygon", mapOf("points" to "2,2 40,12 2,22", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
        )),

        ColorPicture("rocket", "Rocket", "Vehicles", listOf(
            ColorRegion("flame-out", 115, 225, 70, 60, listOf(
                ColorPrim("Polygon", mapOf("points" to "35,55 5,5 25,15 35,2 45,15 65,5", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("flame-in", 130, 230, 40, 40, listOf(
                ColorPrim("Polygon", mapOf("points" to "20,35 5,5 20,10 35,5", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("fin-l", 60, 160, 60, 75, listOf(
                ColorPrim("Path", mapOf("d" to "M 55 5 L 10 65 L 55 55 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("fin-r", 180, 160, 60, 75, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 5 L 50 65 L 5 55 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("body", 100, 30, 100, 185, listOf(
                ColorPrim("Path", mapOf("d" to "M 50 5 C 80 40 85 110 85 180 L 15 180 C 15 110 20 40 50 5 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "4")),
            )),
            ColorRegion("nosecone", 120, 30, 60, 50, listOf(
                ColorPrim("Path", mapOf("d" to "M 30 5 C 45 25 50 45 50 45 L 10 45 C 10 45 15 25 30 5 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("porthole", 125, 95, 50, 50, listOf(
                ColorPrim("Circle", mapOf("cx" to "25", "cy" to "25", "r" to "22", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Circle", mapOf("cx" to "25", "cy" to "25", "r" to "14", "fill" to "#67E8F9", "stroke" to "#111", "strokeWidth" to "2")),
            )),
        )),

        // ==========================================
        // FOOD
        // ==========================================
        ColorPicture("icecream", "Ice Cream", "Food", listOf(
            ColorRegion("cone", 85, 145, 130, 135, listOf(
                ColorPrim("Polygon", mapOf("points" to "65,130 5,5 125,5", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
                ColorPrim("Line", mapOf("x1" to "25", "y1" to "5", "x2" to "85", "y2" to "90", "stroke" to "#111", "strokeWidth" to "2")),
                ColorPrim("Line", mapOf("x1" to "55", "y1" to "5", "x2" to "95", "y2" to "65", "stroke" to "#111", "strokeWidth" to "2")),
                ColorPrim("Line", mapOf("x1" to "105", "y1" to "5", "x2" to "45", "y2" to "90", "stroke" to "#111", "strokeWidth" to "2")),
                ColorPrim("Line", mapOf("x1" to "75", "y1" to "5", "x2" to "35", "y2" to "65", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("scoop-bot", 75, 100, 150, 65, listOf(
                ColorPrim("Path", mapOf("d" to "M 15 50 C 5 25 25 5 75 5 C 125 5 145 25 135 50 C 120 60 100 50 75 55 C 50 50 30 60 15 50 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("scoop-top", 85, 45, 130, 75, listOf(
                ColorPrim("Path", mapOf("d" to "M 15 65 C 5 20 30 5 65 5 C 100 5 125 20 115 65 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("cherry", 135, 20, 35, 40, listOf(
                ColorPrim("Circle", mapOf("cx" to "15", "cy" to "25", "r" to "12", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Path", mapOf("d" to "M 15 13 Q 25 2 30 5", "fill" to "none", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
        )),

        ColorPicture("cake", "Cake", "Food", listOf(
            ColorRegion("plate", 25, 230, 250, 40, listOf(
                ColorPrim("Ellipse", mapOf("cx" to "125", "cy" to "20", "rx" to "120", "ry" to "16", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("layer-1", 50, 160, 200, 75, listOf(
                ColorPrim("Rect", mapOf("x" to "2", "y" to "2", "width" to "196", "height" to "70", "rx" to "8", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("layer-2", 75, 100, 150, 65, listOf(
                ColorPrim("Rect", mapOf("x" to "2", "y" to "2", "width" to "146", "height" to "60", "rx" to "8", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("candle", 140, 50, 20, 55, listOf(
                ColorPrim("Rect", mapOf("x" to "4", "y" to "2", "width" to "12", "height" to "50", "rx" to "2", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("flame", 138, 20, 24, 35, listOf(
                ColorPrim("Path", mapOf("d" to "M 12 2 C 22 15 22 28 12 32 C 2 28 2 15 12 2 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
        )),

        ColorPicture("donut", "Donut", "Food", listOf(
            ColorRegion("dough", 40, 40, 220, 220, listOf(
                ColorPrim("Path", mapOf("d" to "M 110 5 A 105 105 0 1 0 110 215 A 105 105 0 1 0 110 5 Z M 110 70 A 40 40 0 1 1 110 150 A 40 40 0 1 1 110 70 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "4", "fillRule" to "evenodd")),
            )),
            ColorRegion("icing", 45, 45, 210, 210, listOf(
                ColorPrim("Path", mapOf("d" to "M 105 10 C 145 10 185 30 195 70 C 205 110 185 135 190 165 C 195 195 145 200 105 195 C 65 190 35 185 20 150 C 5 115 25 70 45 40 C 65 10 85 10 105 10 Z M 105 65 A 40 40 0 1 1 105 145 A 40 40 0 1 1 105 65 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3", "fillRule" to "evenodd")),
            )),
            ColorRegion("sprinkle-1", 75, 80, 25, 15, listOf(
                ColorPrim("Rect", mapOf("x" to "2", "y" to "2", "width" to "20", "height" to "10", "rx" to "4", "transform" to "rotate(25 12 7)", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("sprinkle-2", 185, 85, 25, 15, listOf(
                ColorPrim("Rect", mapOf("x" to "2", "y" to "2", "width" to "20", "height" to "10", "rx" to "4", "transform" to "rotate(-35 12 7)", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("sprinkle-3", 85, 185, 25, 15, listOf(
                ColorPrim("Rect", mapOf("x" to "2", "y" to "2", "width" to "20", "height" to "10", "rx" to "4", "transform" to "rotate(-20 12 7)", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("sprinkle-4", 175, 175, 25, 15, listOf(
                ColorPrim("Rect", mapOf("x" to "2", "y" to "2", "width" to "20", "height" to "10", "rx" to "4", "transform" to "rotate(40 12 7)", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2")),
            )),
        )),

        ColorPicture("watermelon", "Watermelon", "Food", listOf(
            ColorRegion("rind", 30, 60, 240, 190, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 50 C 35 165 205 165 230 50 L 210 40 C 190 140 50 140 30 40 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("flesh", 45, 55, 210, 155, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 35 C 35 130 175 130 200 35 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("seed-1", 90, 105, 15, 22, listOf(
                ColorPrim("Path", mapOf("d" to "M 7 2 C 14 10 14 18 7 20 C 0 18 0 10 7 2 Z", "fill" to "#111")),
            )),
            ColorRegion("seed-2", 145, 115, 15, 22, listOf(
                ColorPrim("Path", mapOf("d" to "M 7 2 C 14 10 14 18 7 20 C 0 18 0 10 7 2 Z", "fill" to "#111")),
            )),
            ColorRegion("seed-3", 195, 100, 15, 22, listOf(
                ColorPrim("Path", mapOf("d" to "M 7 2 C 14 10 14 18 7 20 C 0 18 0 10 7 2 Z", "fill" to "#111")),
            )),
        )),

        ColorPicture("burger", "Burger", "Food", listOf(
            ColorRegion("top-bun", 35, 45, 230, 95, listOf(
                ColorPrim("Path", mapOf("d" to "M 10 90 C 10 15 220 15 220 90 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "4")),
                ColorPrim("Circle", mapOf("cx" to "65", "cy" to "50", "r" to "3", "fill" to "#FFF")),
                ColorPrim("Circle", mapOf("cx" to "115", "cy" to "40", "r" to "3", "fill" to "#FFF")),
                ColorPrim("Circle", mapOf("cx" to "165", "cy" to "55", "r" to "3", "fill" to "#FFF")),
            )),
            ColorRegion("tomato", 45, 130, 210, 30, listOf(
                ColorPrim("Rect", mapOf("x" to "2", "y" to "2", "width" to "206", "height" to "26", "rx" to "8", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("cheese", 35, 150, 230, 35, listOf(
                ColorPrim("Polygon", mapOf("points" to "5,5 225,5 210,30 145,15 85,30", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("patty", 40, 170, 220, 45, listOf(
                ColorPrim("Rect", mapOf("x" to "2", "y" to "2", "width" to "216", "height" to "40", "rx" to "12", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("bot-bun", 45, 205, 210, 55, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 5 L 205 5 C 205 45 5 45 5 5 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "4")),
            )),
        )),

        // ==========================================
        // ISLAMIC
        // ==========================================
        ColorPicture("crescent", "Crescent & Star", "Islamic", listOf(
            ColorRegion("crescent", 55, 30, 190, 240, listOf(
                ColorPrim("Path", mapOf("d" to "M 95 5 C 165 5 185 85 160 160 C 135 185 90 235 45 230 C 145 195 145 55 25 45 C 45 20 70 5 95 5 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "4")),
            )),
            ColorRegion("star", 155, 95, 75, 75, listOf(
                ColorPrim("Polygon", mapOf("points" to "37,2 47,25 72,25 52,40 60,65 37,50 15,65 22,40 2,25 27,25", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("lantern", 45, 120, 55, 105, listOf(
                ColorPrim("Path", mapOf("d" to "M 27 5 L 27 25 M 10 25 L 45 25 L 50 65 L 27 95 L 5 65 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Circle", mapOf("cx" to "27", "cy" to "55", "r" to "8", "fill" to "#FDE047")),
            )),
        )),

        ColorPicture("mosque", "Mosque", "Islamic", listOf(
            ColorRegion("dome", 85, 45, 130, 95, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 90 C 5 25 65 5 65 5 C 65 5 125 25 125 90 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
                ColorPrim("Polygon", mapOf("points" to "65,2 68,12 62,12", "fill" to "#F59E0B")),
                ColorPrim("Circle", mapOf("cx" to "65", "cy" to "2", "r" to "3", "fill" to "#F59E0B")),
            )),
            ColorRegion("body", 75, 135, 150, 135, listOf(
                ColorPrim("Rect", mapOf("x" to "2", "y" to "2", "width" to "146", "height" to "130", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("door", 125, 175, 50, 95, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 90 L 5 35 C 5 10 45 10 45 35 L 45 90 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("minaret-l", 25, 55, 45, 215, listOf(
                ColorPrim("Rect", mapOf("x" to "5", "y" to "45", "width" to "35", "height" to "166", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Path", mapOf("d" to "M 5 45 C 5 20 22 5 22 5 C 22 5 40 20 40 45 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Rect", mapOf("x" to "2", "y" to "90", "width" to "41", "height" to "10", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("minaret-r", 230, 55, 45, 215, listOf(
                ColorPrim("Rect", mapOf("x" to "5", "y" to "45", "width" to "35", "height" to "166", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Path", mapOf("d" to "M 5 45 C 5 20 22 5 22 5 C 22 5 40 20 40 45 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Rect", mapOf("x" to "2", "y" to "90", "width" to "41", "height" to "10", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
        )),

        ColorPicture("star8", "Eight Star", "Islamic", listOf(
            ColorRegion("star-sq1", 50, 50, 200, 200, listOf(
                ColorPrim("Rect", mapOf("x" to "10", "y" to "10", "width" to "180", "height" to "180", "rx" to "4", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "4")),
            )),
            ColorRegion("star-sq2", 50, 50, 200, 200, listOf(
                ColorPrim("Rect", mapOf("x" to "10", "y" to "10", "width" to "180", "height" to "180", "rx" to "4", "transform" to "rotate(45 100 100)", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "4")),
            )),
            ColorRegion("center-circle", 100, 100, 100, 100, listOf(
                ColorPrim("Circle", mapOf("cx" to "50", "cy" to "50", "r" to "45", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("inner-star", 120, 120, 60, 60, listOf(
                ColorPrim("Polygon", mapOf("points" to "30,2 38,20 58,20 42,32 48,52 30,40 12,52 18,32 2,20 22,20", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
        )),

        // ==========================================
        // OBJECTS
        // ==========================================
        ColorPicture("house", "House", "Objects", listOf(
            ColorRegion("chimney", 195, 30, 40, 65, listOf(
                ColorPrim("Rect", mapOf("x" to "2", "y" to "2", "width" to "36", "height" to "60", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("roof", 30, 50, 240, 95, listOf(
                ColorPrim("Polygon", mapOf("points" to "120,5 235,90 5,90", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "4")),
            )),
            ColorRegion("walls", 50, 140, 200, 130, listOf(
                ColorPrim("Rect", mapOf("x" to "2", "y" to "2", "width" to "196", "height" to "125", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("door", 125, 185, 50, 85, listOf(
                ColorPrim("Rect", mapOf("x" to "2", "y" to "2", "width" to "46", "height" to "82", "rx" to "4", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Circle", mapOf("cx" to "38", "cy" to "45", "r" to "3.5", "fill" to "#111")),
            )),
            ColorRegion("window-l", 70, 165, 45, 45, listOf(
                ColorPrim("Rect", mapOf("x" to "2", "y" to "2", "width" to "41", "height" to "41", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Line", mapOf("x1" to "22", "y1" to "2", "x2" to "22", "y2" to "43", "stroke" to "#111", "strokeWidth" to "2")),
                ColorPrim("Line", mapOf("x1" to "2", "y1" to "22", "x2" to "43", "y2" to "22", "stroke" to "#111", "strokeWidth" to "2")),
            )),
            ColorRegion("window-r", 185, 165, 45, 45, listOf(
                ColorPrim("Rect", mapOf("x" to "2", "y" to "2", "width" to "41", "height" to "41", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "2.5")),
                ColorPrim("Line", mapOf("x1" to "22", "y1" to "2", "x2" to "22", "y2" to "43", "stroke" to "#111", "strokeWidth" to "2")),
                ColorPrim("Line", mapOf("x1" to "2", "y1" to "22", "x2" to "43", "y2" to "22", "stroke" to "#111", "strokeWidth" to "2")),
            )),
        )),

        ColorPicture("balloon", "Balloons", "Objects", listOf(
            ColorRegion("balloon-l", 35, 45, 105, 135, listOf(
                ColorPrim("Path", mapOf("d" to "M 52 5 C 90 5 100 65 75 105 L 52 125 L 30 105 C 5 65 15 5 52 5 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("balloon-r", 160, 45, 105, 135, listOf(
                ColorPrim("Path", mapOf("d" to "M 52 5 C 90 5 100 65 75 105 L 52 125 L 30 105 C 5 65 15 5 52 5 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("balloon-c", 95, 20, 110, 145, listOf(
                ColorPrim("Path", mapOf("d" to "M 55 5 C 95 5 105 70 80 115 L 55 135 L 30 115 C 5 70 15 5 55 5 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
                ColorPrim("Path", mapOf("d" to "M 25 35 Q 20 65 30 85", "fill" to "none", "stroke" to "#FFF", "strokeWidth" to "4")),
            )),
            ColorRegion("strings", 85, 155, 130, 125, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 15 Q 65 70 65 120 M 65 0 L 65 120 M 125 15 Q 65 70 65 120", "fill" to "none", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
        )),

        ColorPicture("present", "Gift Box", "Objects", listOf(
            ColorRegion("box", 40, 110, 220, 155, listOf(
                ColorPrim("Rect", mapOf("x" to "2", "y" to "2", "width" to "216", "height" to "150", "rx" to "6", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "4")),
            )),
            ColorRegion("lid", 30, 85, 240, 40, listOf(
                ColorPrim("Rect", mapOf("x" to "2", "y" to "2", "width" to "236", "height" to "36", "rx" to "6", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "4")),
            )),
            ColorRegion("ribbon-v", 130, 85, 40, 180, listOf(
                ColorPrim("Rect", mapOf("x" to "2", "y" to "2", "width" to "36", "height" to "176", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("bow-l", 75, 30, 75, 65, listOf(
                ColorPrim("Path", mapOf("d" to "M 70 55 C 20 65 5 35 15 15 C 30 -5 65 25 70 55 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("bow-r", 150, 30, 75, 65, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 55 C 55 65 70 35 60 15 C 45 -5 10 25 5 55 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("bow-center", 135, 65, 30, 30, listOf(
                ColorPrim("Circle", mapOf("cx" to "15", "cy" to "15", "r" to "13", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
        )),

        // ==========================================
        // SHAPES
        // ==========================================
        ColorPicture("star", "Star", "Shapes", listOf(
            ColorRegion("center", 90, 90, 120, 120, listOf(
                ColorPrim("Polygon", mapOf("points" to "60,5 98,33 83,78 37,78 22,33", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
                ColorPrim("Circle", mapOf("cx" to "45", "cy" to "45", "r" to "4", "fill" to "#111")),
                ColorPrim("Circle", mapOf("cx" to "75", "cy" to "45", "r" to "4", "fill" to "#111")),
                ColorPrim("Path", mapOf("d" to "M 50 60 Q 60 70 70 60", "fill" to "none", "stroke" to "#111", "strokeWidth" to "2.5")),
            )),
            ColorRegion("tip-top", 115, 20, 70, 75, listOf(
                ColorPrim("Polygon", mapOf("points" to "35,5 65,70 5,70", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("tip-tr", 175, 65, 85, 70, listOf(
                ColorPrim("Polygon", mapOf("points" to "80,35 5,5 20,65", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("tip-br", 160, 160, 85, 95, listOf(
                ColorPrim("Polygon", mapOf("points" to "65,85 5,5 75,15", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("tip-bl", 55, 160, 85, 95, listOf(
                ColorPrim("Polygon", mapOf("points" to "20,85 10,15 80,5", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("tip-tl", 40, 65, 85, 70, listOf(
                ColorPrim("Polygon", mapOf("points" to "5,35 65,65 80,5", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
        )),

        ColorPicture("heart", "Heart", "Shapes", listOf(
            ColorRegion("wing-l", 20, 75, 85, 95, listOf(
                ColorPrim("Path", mapOf("d" to "M 80 50 C 40 20 10 40 5 70 C 25 80 60 70 80 50 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("wing-r", 195, 75, 85, 95, listOf(
                ColorPrim("Path", mapOf("d" to "M 5 50 C 45 20 75 40 80 70 C 60 80 25 70 5 50 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("body", 65, 50, 170, 190, listOf(
                ColorPrim("Path", mapOf("d" to "M 85 180 C 10 100 20 20 85 50 C 150 20 160 100 85 180 Z", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "4")),
                ColorPrim("Path", mapOf("d" to "M 45 50 C 35 70 45 100 65 120", "fill" to "none", "stroke" to "#FFF", "strokeWidth" to "4")),
            )),
        )),

        ColorPicture("diamond", "Diamond Gem", "Shapes", listOf(
            ColorRegion("table", 90, 45, 120, 50, listOf(
                ColorPrim("Polygon", mapOf("points" to "20,5 100,5 115,45 5,45", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("facet-tl", 40, 45, 55, 50, listOf(
                ColorPrim("Polygon", mapOf("points" to "50,5 5,45 50,45", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("facet-tr", 205, 45, 55, 50, listOf(
                ColorPrim("Polygon", mapOf("points" to "5,5 5,45 50,45", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("pavilion-mid", 90, 95, 120, 155, listOf(
                ColorPrim("Polygon", mapOf("points" to "5,5 115,5 60,150", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3.5")),
            )),
            ColorRegion("pavilion-l", 45, 95, 50, 155, listOf(
                ColorPrim("Polygon", mapOf("points" to "45,5 5,5 45,150", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
            ColorRegion("pavilion-r", 205, 95, 50, 155, listOf(
                ColorPrim("Polygon", mapOf("points" to "5,5 45,5 5,150", "fill" to "__FILL__", "stroke" to "#111", "strokeWidth" to "3")),
            )),
        ))
    )
}
