#!/usr/bin/env kotlin

class Type(val name: String) {
    val ref get() = Type("$name*")
    override fun toString() = name
}

val None = Type("void")
val Byte = Type("unsigned char")
val IByte = Type("signed char")
val Half = Type("unsigned short")
val IHalf = Type("signed short")
val Word = Type("unsigned int")
val IWord = Type("signed int")

class Variable(val type: Type, val name: String)

class Function(val address: UInt, val name: String) {
    class Register(val index: Int, val variable: Variable)

    val parameters = MutableList<Variable?>(32) { null }
    val returns = MutableList<Variable?>(32) { null }

    fun parameter(type: Type, name: String) {
        parameter(parameters.indexOfFirst { it == null }, type, name)
    }
    fun parameter(index: Int, type: Type, name: String) {
        parameters[index] = Variable(type, name)
    }

    fun returns(type: Type) {
        returns(returns.indexOfFirst { it == null }, type)
    }
    fun returns(index: Int, type: Type) {
        returns[index] = Variable(type, "result_${index}")
    }

    override fun toString(): String {
        val text = StringBuilder()

        val inputs = parameters
            .mapIndexedNotNull { index, variable -> variable?.let { Register(index, it) } }
        val outputs = returns
            .mapIndexedNotNull { index, variable -> variable?.let { Register(index, it) } }

        text.append("static inline ")

        val returns = outputs.firstOrNull()
        if (returns != null) {
            text.append(returns.variable.type)
        } else {
            text.append(None)
        }

        text.append(" ")
        text.append(name)
        text.append("(")

        if (inputs.isNotEmpty()) {
            text.append("\n")
            text.append(inputs.joinToString(",\n") { "    ${it.variable.type} ${it.variable.name}" })
            text.append("\n) {\n")
        } else {
            text.append(None)
            text.append(") {\n")
        }

        outputs.forEach {
            text.append("    ")
            text.append(it.variable.type)
            text.append(" ")
            text.append(it.variable.name)
            text.append(";\n")
        }

        inputs.forEach {
            text.append("    ")
            text.append("parameter(")
            text.append(it.index)
            text.append(", ")
            text.append(it.variable.name)
            text.append(");\n")
        }

        text.append("    ")
        text.append("call(")
        text.append("0x%08X".format(address.toInt()))
        text.append(");\n")

        outputs.forEach {
            text.append("    ")
            text.append("ret(")
            text.append(it.index)
            text.append(", ")
            text.append(it.variable.name)
            text.append(");\n")
        }

        if (returns != null) {
            text.append("    ")
            text.append("return ")
            text.append(returns.variable.name)
            text.append(";\n")
        }

        text.append("}\n")

        return text.toString()
    }
}

val source = StringBuilder()

source.append("#pragma once\n")
source.append("\n")
source.append("#include \"call.h\"\n")
source.append("\n")

fun comment(text: String) {
    source.append("// ")
    source.append(text)
    source.append("\n\n")
}

fun constant(name: String, value: UInt) {
    source.append("#define ")
    source.append(name)
    source.append(" ")
    source.append("0x%08X".format(value.toInt()))
    source.append("\n\n")
}

fun define(address: UInt, name: String, block: Function.() -> Unit) {
    Function(address, name)
        .also(block)
        .also {
            source.append(it)
            source.append("\n")
        }
}

// BEGIN FUNCTION DEFINITIONS

comment("fox32rom definitions")

comment("system jump table")

define(0xF0040000U, "get_rom_version") {
}
define(0xF0040004U, "system_vsync_handler") {
}
define(0xF0040008U, "get_mouse_position") {
}
define(0xF004000CU, "new_event") {
}
define(0xF0040010U, "wait_for_event") {
}
define(0xF0040014U, "get_next_event") {
}
define(0xF0040018U, "panic") {
}
define(0xF004001CU, "get_mouse_button") {
}
define(0xF0040020U, "scancode_to_ascii") {
}
define(0xF0040024U, "shift_pressed") {
}
define(0xF0040028U, "shift_released") {
}
define(0xF004002CU, "caps_pressed") {
}
define(0xF0040030U, "poweroff") {
}

comment("generic drawing jump table")

define(0xF0041000U, "draw_str_generic") {
}
define(0xF0041004U, "draw_format_str_generic") {
}
define(0xF0041008U, "draw_decimal_generic") {
}
define(0xF004100CU, "draw_hex_generic") {
}
define(0xF0041010U, "draw_font_tile_generic") {
}
define(0xF0041014U, "draw_tile_generic") {
}
define(0xF0041018U, "set_tilemap") {
}
define(0xF004101CU, "draw_pixel_generic") {
}
define(0xF0041020U, "draw_filled_rectangle_generic") {
}
define(0xF0041024U, "get_tilemap") {
}

comment("background jump table")

define(0xF0042000U, "fill_background") {
    parameter(Word, "color")
}
define(0xF0042004U, "draw_str_to_background") {
    parameter(Byte.ref, "str")
    parameter(Word, "x")
    parameter(Word, "y")
    parameter(Word, "foreground_color")
    parameter(Word, "background_color")
    returns(1, Word)
}
define(0xF0042008U, "draw_format_str_to_background") {
    parameter(Byte.ref, "str")
    parameter(Word, "x")
    parameter(Word, "y")
    parameter(Word, "foreground_color")
    parameter(Word, "background_color")
    parameter(10, Word, "format_value_0")
    parameter(11, Word, "format_value_1")
    parameter(12, Word, "format_value_2")
    parameter(13, Word, "format_value_3")
    parameter(14, Word, "format_value_4")
    parameter(15, Word, "format_value_5")
    returns(1, Word)
}
define(0xF004200CU, "draw_decimal_to_background") {
    parameter(Word, "value")
    parameter(Word, "x")
    parameter(Word, "y")
    parameter(Word, "foreground_color")
    parameter(Word, "background_color")
    returns(1, Word)
}
define(0xF0042010U, "draw_hex_to_background") {
    parameter(Word, "value")
    parameter(Word, "x")
    parameter(Word, "y")
    parameter(Word, "foreground_color")
    parameter(Word, "background_color")
    returns(1, Word)
}
define(0xF0042014U, "draw_font_tile_to_background") {
    parameter(Word, "tile")
    parameter(Word, "x")
    parameter(Word, "y")
    parameter(Word, "foreground_color")
    parameter(Word, "background_color")
}
define(0xF0042018U, "draw_tile_to_background") {
    parameter(Word, "tile")
    parameter(Word, "x")
    parameter(Word, "y")
}
define(0xF004201CU, "draw_pixel_to_background") {
    parameter(Word, "x")
    parameter(Word, "y")
    parameter(Word, "color")
}
define(0xF0042020U, "draw_filled_rectangle_to_background") {
    parameter(Word, "x")
    parameter(Word, "y")
    parameter(Word, "width")
    parameter(Word, "height")
    parameter(Word, "color")
}

comment("overlay jump table")

define(0xF0043000U, "fill_overlay") {
}
define(0xF0043004U, "draw_str_to_overlay") {
}
define(0xF0043008U, "draw_format_str_to_overlay") {
}
define(0xF004300CU, "draw_decimal_to_overlay") {
}
define(0xF0043010U, "draw_hex_to_overlay") {
}
define(0xF0043014U, "draw_font_tile_to_overlay") {
}
define(0xF0043018U, "draw_tile_to_overlay") {
}
define(0xF004301CU, "draw_pixel_to_overlay") {
}
define(0xF0043020U, "draw_filled_rectangle_to_overlay") {
}
define(0xF0043024U, "check_if_overlay_covers_position") {
}
define(0xF0043028U, "check_if_enabled_overlay_covers_position") {
}
define(0xF004302CU, "enable_overlay") {
}
define(0xF0043030U, "disable_overlay") {
}
define(0xF0043034U, "move_overlay") {
}
define(0xF0043038U, "resize_overlay") {
}
define(0xF004303CU, "set_overlay_framebuffer_pointer") {
}
define(0xF0043040U, "get_unused_overlay") {
}
define(0xF0043044U, "make_coordinates_relative_to_overlay") {
}

comment("menu bar jump table")

define(0xF0044000U, "enable_menu_bar") {
}
define(0xF0044004U, "disable_menu_bar") {
}
define(0xF0044008U, "menu_bar_click_event") {
}
define(0xF004400CU, "clear_menu_bar") {
}
define(0xF0044010U, "draw_menu_bar_root_items") {
}
define(0xF0044014U, "draw_menu_items") {
}
define(0xF0044018U, "close_menu") {
}
define(0xF004401CU, "menu_update_event") {
}

comment("disk jump table")

define(0xF0045000U, "read_sector") {
}
define(0xF0045004U, "write_sector") {
}
define(0xF0045008U, "ryfs_open") {
}
define(0xF004500CU, "ryfs_seek") {
}
define(0xF0045010U, "ryfs_read") {
}
define(0xF0045014U, "ryfs_read_whole_file") {
}
define(0xF0045018U, "ryfs_get_size") {
}
define(0xF004501CU, "ryfs_get_file_list") {
}
define(0xF0045020U, "ryfs_tell") {
}
define(0xF0045024U, "ryfs_write") {
}

comment("memory copy/compare jump table")

define(0xF0046000U, "copy_memory_bytes") {
}
define(0xF0046004U, "copy_memory_words") {
}
define(0xF0046008U, "copy_string") {
}
define(0xF004600CU, "compare_memory_bytes") {
}
define(0xF0046010U, "compare_memory_words") {
}
define(0xF0046014U, "compare_string") {
}
define(0xF0046018U, "string_length") {
}

comment("integer jump table")

define(0xF0047000U, "string_to_int") {
}

comment("audio jump table")

define(0xF0048000U, "play_audio") {
}
define(0xF0048004U, "stop_audio") {
}

comment("random number jump table")

define(0xF0049000U, "random") {
    returns(Word)
}
define(0xF0049004U, "random_range") {
    parameter(1, Word, "minimum")
    parameter(2, Word, "maximum")
    returns(Word)
}

comment("keys")

constant("KEY_CTRL", 0x1DU)
constant("KEY_LSHIFT", 0x2AU)
constant("KEY_RSHIFT", 0x36U)
constant("KEY_CAPS", 0x3AU)

comment("fox32os definitions")

comment("system jump table")

define(0x00000810U, "get_os_version") {
}

comment("FXF jump table")

define(0x00000910U, "parse_fxf_binary") {
}

comment("task jump table")

define(0x00000A10U, "new_task") {
}
define(0x00000A14U, "yield_task") {
}
define(0x00000A18U, "end_current_task") {
}
define(0x00000A1CU, "get_current_task_id") {
}
define(0x00000A20U, "get_unused_task_id") {
}
define(0x00000A24U, "is_task_id_used") {
}

comment("memory jump table")

define(0x00000B10U, "allocate_memory") {
}
define(0x00000B14U, "free_memory") {
}

comment("window jump table")

define(0x00000C10U, "new_window") {
}
define(0x00000C14U, "destroy_window") {
}
define(0x00000C18U, "new_window_event") {
}
define(0x00000C1CU, "get_next_window_event") {
}
define(0x00000C20U, "draw_title_bar_to_window") {
}
define(0x00000C24U, "move_window") {
}
define(0x00000C28U, "fill_window") {
}
define(0x00000C2CU, "get_window_overlay_number") {
}
define(0x00000C30U, "start_dragging_window") {
}
define(0x00000C34U, "new_messagebox") {
}
define(0x00000C38U, "get_active_window_struct") {
}

comment("VFS jump table")

define(0x00000D10U, "open") {
}
define(0x00000D14U, "seek") {
}
define(0x00000D18U, "tell") {
}
define(0x00000D1CU, "read") {
}
define(0x00000D20U, "write") {
}

comment("widget jump table")

define(0x00000E10U, "draw_widgets_to_window") {
}
define(0x00000E14U, "handle_widget_click") {
}

comment("event types")

constant("EVENT_TYPE_MOUSE_CLICK", 0x00000000U)
constant("EVENT_TYPE_MOUSE_RELEASE", 0x00000001U)
constant("EVENT_TYPE_KEY_DOWN", 0x00000002U)
constant("EVENT_TYPE_KEY_UP", 0x00000003U)
constant("EVENT_TYPE_MENU_BAR_CLICK", 0x00000004U)
constant("EVENT_TYPE_MENU_UPDATE", 0x00000005U)
constant("EVENT_TYPE_MENU_CLICK", 0x00000006U)
constant("EVENT_TYPE_MENU_ACK", 0x00000007U)
constant("EVENT_TYPE_BUTTON_CLICK", 0x80000000U)
constant("EVENT_TYPE_EMPTY", 0xFFFFFFFFU)

comment("widget types")

constant("WIDGET_TYPE_BUTTON", 0x00000000U)

// END FUNCTION DEFINITIONS

println(source)
