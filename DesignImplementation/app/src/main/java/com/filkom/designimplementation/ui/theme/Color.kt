package com.filkom.designimplementation.ui.theme

import androidx.compose.ui.graphics.Color

// ================= PRIMARY (Pink/Rose) =================
// Digunakan untuk Brand Identity, Tombol Utama, Highlight
val Primary50  = Color(0xFFFDF2F8)
val Primary100 = Color(0xFFFCE7F3)
val Primary200 = Color(0xFFFBCFE8)
val Primary300 = Color(0xFFF9A8D4)
val Primary400 = Color(0xFFF472B6) // <-- Primary Pink Nya
val Primary500 = Color(0xFFEC4899)
val Primary600 = Color(0xFFDB2777)
val Primary700 = Color(0xFFBE185D)
val Primary800 = Color(0xFF9D174D)
val Primary900 = Color(0xFF831843)

// ================= SECONDARY (Purple/Violet) =================
// Digunakan untuk aksen sekunder, ilustrasi, atau state aktif tertentu
val Secondary50  = Color(0xFFF5F3FF)
val Secondary100 = Color(0xFFEDE9FE)
val Secondary200 = Color(0xFFDDD6FE)
val Secondary300 = Color(0xFFC4B5FD)
val Secondary400 = Color(0xFFA78BFA)
val Secondary500 = Color(0xFF8B5CF6)
val Secondary600 = Color(0xFF7C3AED)
val Secondary700 = Color(0xFF6D28D9)
val Secondary800 = Color(0xFF5B21B6)
val Secondary900 = Color(0xFF4C1D95)

// ================= NEUTRAL W (Warm Grays/White) =================
// Digunakan untuk Background, Surface, Border tipis
val Neutral50  = Color(0xFFFAFAFA)
val Neutral100 = Color(0xFFF5F5F5)
val Neutral200 = Color(0xFFE5E5E5)
val Neutral300 = Color(0xFFD4D4D4)
val Neutral400 = Color(0xFFA3A3A3)
val Neutral500 = Color(0xFF737373)
val Neutral600 = Color(0xFF525252)
val Neutral700 = Color(0xFF404040)
val Neutral800 = Color(0xFF262626)
val Neutral900 = Color(0xFF171717)

// ================= BLACK (Cool Grays/Slate) =================
// Digunakan untuk Teks Utama, Heading, Icon Gelap
val Black50  = Color(0xFFF8FAFC)
val Black100 = Color(0xFFF1F5F9)
val Black200 = Color(0xFFE2E8F0)
val Black300 = Color(0xFFCBD5E1)
val Black400 = Color(0xFF94A3B8)
val Black500 = Color(0xFF64748B)
val Black600 = Color(0xFF475569)
val Black700 = Color(0xFF334155)
val Black800 = Color(0xFF1E293B)
val Black900 = Color(0xFF0F172A) // <-- Teks paling gelap (TextClr)

// ================= ERROR (Red) =================
// Digunakan untuk pesan error, tombol hapus, validasi gagal
val Error50  = Color(0xFFFEF2F2)
val Error100 = Color(0xFFFEE2E2)
val Error200 = Color(0xFFFECACA)
val Error300 = Color(0xFFFCA5A5)
val Error400 = Color(0xFFF87171)
val Error500 = Color(0xFFEF4444)
val Error600 = Color(0xFFDC2626)
val Error700 = Color(0xFFB91C1C)
val Error800 = Color(0xFF991B1B)
val Error900 = Color(0xFF7F1D1D)

// ================= SUCCESS (Green) =================
// Digunakan untuk pesan sukses, status berhasil, badge aktif
val Success50  = Color(0xFFF0FDF4)
val Success100 = Color(0xFFDCFCE7)
val Success200 = Color(0xFFBBF7D0)
val Success300 = Color(0xFF86EFAC)
val Success400 = Color(0xFF4ADE80)
val Success500 = Color(0xFF22C55E)
val Success600 = Color(0xFF16A34A)
val Success700 = Color(0xFF15803D)
val Success800 = Color(0xFF166534)
val Success900 = Color(0xFF14532D)

// ================= MAPPING KE KODE LAMA ANDA =================
// Tambahkan ini agar kode lama (Pink, TextClr, dll) tidak error
// dan otomatis menggunakan palet baru ini.

val TextClr = Black900       // Menggantikan warna Text hitam lama
val TextGray = Black500      // Warna teks abu-abu
val BorderClr = Neutral200   // Warna garis batas/border
val Red = Primary400        // Menggantikan warna Pink lama
val Green = Success600
// Gradient Helpers (Opsional, diambil dari Primary & Secondary)
val Pink = Primary400        // Menggantikan warna Pink lama
val PinkPrimary = Color(0xFFFF69B4) // Pink Utama
val PinkSoft = Color(0xFFFFF0F5)     // Pink Background Lembut
val PinkSurface = Color(0xFFFFFBFC)  // Background Halaman
val TextPrimary = Color(0xFF1A1A1A)  // Hitam tidak pekat (lebih enak di mata)
val TextSecondary = Color(0xFF757575)
val LightPinkBg = Color(0xFFFFF0F5)
val SoftGray = Color(0xFFF8F9FA)
private val PageBg = Color(0xFFFFF1F6)
private val InputBg = Color(0xFFFFFFFF)

val primaryGradient= listOf(
    Color(0xFFa75fbf),
    Color(0xFFa960bf),
    Color(0xFFC16CC0),
    Color(0xFFc86fc1),
    Color(0xFFD073C2)
)


val lightPurpleGradient= listOf(
    Color(0xFF9c70ff),
    Color(0xFFc27dfc),
    Color(0xFFce7dfe),
    Color(0xFFc86fc1),
    Color(0xFFD93b4fd)
)