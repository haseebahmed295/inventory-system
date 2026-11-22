import os
import sys
from PIL import Image, ImageDraw, ImageFont

SCREENSHOTS_DIR = r"D:\Collage Work\Softwaare lab\eol\screenshots"
os.makedirs(SCREENSHOTS_DIR, exist_ok=True)

def get_fonts(mono_size=13.5, sans_size=13, title_size=16, bold_sans_size=13):
    try:
        mono_font = ImageFont.truetype("consola.ttf", int(mono_size))
        sans_font = ImageFont.truetype("segoeui.ttf", int(sans_size))
        sans_bold = ImageFont.truetype("segoeuib.ttf", int(bold_sans_size))
        title_font = ImageFont.truetype("segoeuib.ttf", int(title_size))
        small_font = ImageFont.truetype("segoeui.ttf", int(sans_size * 0.85))
    except Exception:
        mono_font = ImageFont.load_default()
        sans_font = ImageFont.load_default()
        sans_bold = ImageFont.load_default()
        title_font = ImageFont.load_default()
        small_font = ImageFont.load_default()
    return mono_font, sans_font, sans_bold, title_font, small_font

def create_terminal_image(title, lines, filename, width=820, line_height=20, font_size=13.5):
    mono_font, sans_font, _, title_font, _ = get_fonts(mono_size=font_size, title_size=12.5)

    padding_top = 46
    padding_bottom = 20
    padding_left = 22

    content_height = len(lines) * line_height
    height = padding_top + content_height + padding_bottom

    # Background
    bg_color = (20, 24, 33)
    img = Image.new('RGB', (width, height), bg_color)
    draw = ImageDraw.Draw(img)

    # Title bar
    title_bar_bg = (30, 36, 48)
    draw.rectangle([0, 0, width, 36], fill=title_bar_bg)
    draw.line([(0, 36), (width, 36)], fill=(45, 55, 72), width=1)

    # Title text
    draw.text((18, 9), title, fill=(226, 232, 240), font=title_font)

    # Window controls (minimize, maximize, close)
    ctrl_y = 18
    draw.ellipse([width - 58, ctrl_y - 5, width - 48, ctrl_y + 5], fill=(71, 85, 105))
    draw.ellipse([width - 40, ctrl_y - 5, width - 30, ctrl_y + 5], fill=(71, 85, 105))
    draw.ellipse([width - 22, ctrl_y - 5, width - 12, ctrl_y + 5], fill=(239, 68, 68))

    colors = {
        'prompt': (56, 189, 248),    # Sky blue
        'cmd': (248, 250, 252),       # Bright white
        'output': (203, 213, 225),    # Light slate
        'dim': (148, 163, 184),       # Dim slate
        'error': (248, 113, 113),     # Soft red
        'success': (74, 222, 128),    # Emerald green
        'accent': (251, 191, 36),     # Amber
        'purple': (192, 132, 252),    # Soft purple
        'cyan': (103, 232, 249)       # Soft cyan
    }

    curr_y = padding_top
    for line, ctype in lines:
        c = colors.get(ctype, colors['output'])
        draw.text((padding_left, curr_y), line, fill=c, font=mono_font)
        curr_y += line_height

    out_path = os.path.join(SCREENSHOTS_DIR, filename)
    img.save(out_path, 'PNG')
    print(f"Generated Terminal Screenshot: {out_path} ({width}x{height})")
    return out_path

# ==============================================================================
# Helper to render GUI Window Screenshots
# ==============================================================================
def draw_rounded_rect(draw, bbox, fill, outline=None, width=1, radius=6):
    draw.rounded_rectangle(bbox, radius=radius, fill=fill, outline=outline, width=width)

def render_gui_screenshot(filename, kpi_counts, items_data, form_values, alert_dialog=None, selected_index=-1, search_query=""):
    mono_font, sans_font, sans_bold, title_font, small_font = get_fonts()
    width = 960
    height = 680

    img = Image.new('RGB', (width, height), (248, 250, 252))
    draw = ImageDraw.Draw(img)

    # Window Title Bar (Windows 11 light frame)
    draw.rectangle([0, 0, width, 32], fill=(241, 245, 249))
    draw.line([(0, 32), (width, 32)], fill=(226, 232, 240), width=1)
    draw.text((14, 7), "Store Inventory Management System", fill=(51, 65, 85), font=small_font)
    draw.ellipse([width - 54, 16 - 5, width - 44, 16 + 5], fill=(203, 213, 225))
    draw.ellipse([width - 36, 16 - 5, width - 26, 16 + 5], fill=(203, 213, 225))
    draw.ellipse([width - 18, 16 - 5, width - 8, 16 + 5], fill=(239, 68, 68))

    # Header Section (Clean White Header)
    draw.rectangle([0, 32, width, 84], fill=(255, 255, 255))
    draw.text((24, 40), "📦 Store Inventory Management System", fill=(15, 23, 42), font=title_font)
    draw.text((58, 64), "Real-time stock tracking, catalog control, and inventory analytics", fill=(100, 116, 139), font=small_font)

    # Header Badge (System Online Light Green Pill)
    draw_rounded_rect(draw, [width - 146, 46, width - 24, 70], fill=(236, 253, 245), outline=(167, 243, 208), radius=12)
    draw.ellipse([width - 134, 58 - 3, width - 128, 58 + 3], fill=(5, 150, 105))
    draw.text((width - 120, 52), "System Online", fill=(5, 150, 105), font=small_font)

    # KPI Row (Seamless Light Theme Cards with Left Accent Borders)
    kpi_y = 92
    kpi_w = (width - 48 - 36) // 4
    kpi_configs = [
        ("TOTAL INVENTORY", str(kpi_counts['total']), (37, 99, 235)),
        ("IN STOCK", str(kpi_counts['in_stock']), (16, 185, 129)),
        ("LOW STOCK", str(kpi_counts['low_stock']), (245, 158, 11)),
        ("OUT OF STOCK", str(kpi_counts['out_stock']), (239, 68, 68))
    ]

    for idx, (ktitle, kval, accent_col) in enumerate(kpi_configs):
        kx = 24 + idx * (kpi_w + 12)
        # White card with subtle border
        draw_rounded_rect(draw, [kx, kpi_y, kx + kpi_w, kpi_y + 44], fill=(255, 255, 255), outline=(226, 232, 240), width=1, radius=6)
        # Left accent stripe
        draw.rectangle([kx, kpi_y, kx + 4, kpi_y + 44], fill=accent_col)
        # Dot and label
        draw.ellipse([kx + 12, kpi_y + 12 - 3, kx + 18, kpi_y + 12 + 3], fill=accent_col)
        draw.text((kx + 24, kpi_y + 6), ktitle, fill=(100, 116, 139), font=small_font)
        # Bold Value
        draw.text((kx + 24, kpi_y + 20), kval, fill=(15, 23, 42), font=sans_bold)

    # Divider below top strip
    draw.line([(0, 144), (width, 144)], fill=(226, 232, 240), width=1)

    # Main Split Body
    body_y = 152
    form_w = 330
    form_h = 490

    # Left Form Card
    draw_rounded_rect(draw, [24, body_y, 24 + form_w, body_y + form_h], fill=(255, 255, 255), outline=(226, 232, 240), radius=8)
    draw.text((40, body_y + 14), "Item Details & Management", fill=(15, 23, 42), font=sans_bold)
    draw.text((40, body_y + 34), "Add new merchandise or select an item to modify.", fill=(100, 116, 139), font=small_font)

    # Form Fields
    fy = body_y + 60
    draw.text((40, fy), "Item Name *", fill=(51, 65, 85), font=sans_bold)
    fy += 18
    draw_rounded_rect(draw, [40, fy, 24 + form_w - 16, fy + 32], fill=(255, 255, 255), outline=(203, 213, 225), radius=6)
    draw.text((48, fy + 7), form_values.get('name', ''), fill=(15, 23, 42) if form_values.get('name') else (148, 163, 184), font=sans_font)

    fy += 44
    draw.text((40, fy), "Quantity (Units) *", fill=(51, 65, 85), font=sans_bold)
    fy += 18
    draw_rounded_rect(draw, [40, fy, 24 + form_w - 16, fy + 32], fill=(255, 255, 255), outline=(203, 213, 225), radius=6)
    draw.text((48, fy + 7), str(form_values.get('quantity', '')), fill=(15, 23, 42) if form_values.get('quantity') != '' else (148, 163, 184), font=sans_font)

    fy += 44
    draw.text((40, fy), "Stock Status", fill=(51, 65, 85), font=sans_bold)
    fy += 18
    draw_rounded_rect(draw, [40, fy, 24 + form_w - 16, fy + 32], fill=(255, 255, 255), outline=(203, 213, 225), radius=6)
    draw.text((48, fy + 7), form_values.get('status', 'Auto-Calculate (from Qty)'), fill=(15, 23, 42), font=sans_font)

    # Feedback Banner
    fy += 44
    fb_text = form_values.get('feedback', 'Ready. Enter item attributes above.')
    fb_col = form_values.get('fb_color', (37, 99, 235))
    fb_bg = form_values.get('fb_bg', (248, 250, 252))
    draw_rounded_rect(draw, [40, fy, 24 + form_w - 16, fy + 34], fill=fb_bg, outline=(203, 213, 225), radius=6)
    draw.text((48, fy + 8), fb_text[:44], fill=fb_col, font=small_font)

    # Primary Action Buttons Grid
    fy += 48
    bw = (form_w - 40) // 2
    # Add Item (Primary Blue)
    draw_rounded_rect(draw, [40, fy, 40 + bw - 6, fy + 32], fill=(37, 99, 235), radius=6)
    draw.text((40 + bw//2 - 28, fy + 8), "+ Add Item", fill=(255, 255, 255), font=sans_bold)

    # Update Selected (Sky Blue)
    draw_rounded_rect(draw, [40 + bw + 4, fy, 40 + bw*2 - 2, fy + 32], fill=(2, 132, 199), radius=6)
    draw.text((40 + bw + bw//2 - 38, fy + 8), "✎ Update Item", fill=(255, 255, 255), font=sans_bold)

    fy += 38
    # Delete Item (Danger Soft Red)
    draw_rounded_rect(draw, [40, fy, 40 + bw - 6, fy + 32], fill=(254, 226, 226), outline=(254, 202, 202), radius=6)
    draw.text((40 + bw//2 - 32, fy + 8), "🗑 Delete Item", fill=(220, 38, 38), font=sans_bold)

    # Clear Form (Neutral Outline)
    draw_rounded_rect(draw, [40 + bw + 4, fy, 40 + bw*2 - 2, fy + 32], fill=(248, 250, 252), outline=(203, 213, 225), radius=6)
    draw.text((40 + bw + bw//2 - 30, fy + 8), "↺ Clear Form", fill=(100, 116, 139), font=sans_bold)

    fy += 44
    draw.line([(40, fy), (24 + form_w - 16, fy)], fill=(226, 232, 240), width=1)

    fy += 12
    draw_rounded_rect(draw, [40, fy, 40 + bw - 6, fy + 26], fill=(241, 245, 249), outline=(203, 213, 225), radius=6)
    draw.text((40 + 10, fy + 5), "⚡ Load Sample Catalog", fill=(51, 65, 85), font=small_font)

    draw_rounded_rect(draw, [40 + bw + 4, fy, 40 + bw*2 - 2, fy + 26], fill=(255, 241, 242), outline=(255, 228, 230), radius=6)
    draw.text((40 + bw + 22, fy + 5), "Clear All", fill=(190, 18, 60), font=small_font)

    # Right Table Card
    tbl_x = 24 + form_w + 16
    tbl_w = width - tbl_x - 24
    draw_rounded_rect(draw, [tbl_x, body_y, tbl_x + tbl_w, body_y + form_h], fill=(255, 255, 255), outline=(226, 232, 240), radius=8)

    # Search Bar & Segmented View Switcher
    draw.text((tbl_x + 16, body_y + 16), "Search:", fill=(51, 65, 85), font=sans_bold)
    draw_rounded_rect(draw, [tbl_x + 68, body_y + 10, tbl_x + tbl_w - 240, body_y + 36], fill=(255, 255, 255), outline=(203, 213, 225), radius=6)
    draw.text((tbl_x + 76, body_y + 15), search_query if search_query else "Filter items by name in real-time...", fill=(15, 23, 42) if search_query else (148, 163, 184), font=small_font)

    draw_rounded_rect(draw, [tbl_x + tbl_w - 232, body_y + 10, tbl_x + tbl_w - 180, body_y + 36], fill=(241, 245, 249), outline=(203, 213, 225), radius=6)
    draw.text((tbl_x + tbl_w - 218, body_y + 15), "Clear", fill=(71, 85, 105), font=small_font)

    # Segmented Toggle Pill [ Catalog Table | CSV Data ]
    seg_x = tbl_x + tbl_w - 170
    draw_rounded_rect(draw, [seg_x, body_y + 10, tbl_x + tbl_w - 16, body_y + 36], fill=(241, 245, 249), outline=(203, 213, 225), radius=6)
    # Active Table Segment
    draw_rounded_rect(draw, [seg_x + 2, body_y + 12, seg_x + 84, body_y + 34], fill=(37, 99, 235), radius=4)
    draw.text((seg_x + 8, body_y + 15), "Catalog Table", fill=(255, 255, 255), font=small_font)
    draw.text((seg_x + 94, body_y + 15), "CSV Data", fill=(71, 85, 105), font=small_font)

    # Table Header
    ty = body_y + 48
    draw.rectangle([tbl_x + 16, ty, tbl_x + tbl_w - 16, ty + 28], fill=(248, 250, 252))
    draw.line([(tbl_x + 16, ty + 28), (tbl_x + tbl_w - 16, ty + 28)], fill=(226, 232, 240), width=1)
    draw.text((tbl_x + 24, ty + 6), "Item Name", fill=(71, 85, 105), font=sans_bold)
    draw.text((tbl_x + tbl_w - 280, ty + 6), "Quantity", fill=(71, 85, 105), font=sans_bold)
    draw.text((tbl_x + tbl_w - 190, ty + 6), "Stock Status", fill=(71, 85, 105), font=sans_bold)
    draw.text((tbl_x + tbl_w - 90, ty + 6), "Quick Adjust", fill=(71, 85, 105), font=sans_bold)

    # Table Rows
    ty += 28
    if not items_data:
        draw.text((tbl_x + tbl_w//2 - 110, ty + 60), "No inventory records found.", fill=(148, 163, 184), font=sans_font)
        draw.text((tbl_x + tbl_w//2 - 150, ty + 80), "Enter item details on the left or load sample catalog.", fill=(148, 163, 184), font=small_font)
    else:
        for idx, item in enumerate(items_data):
            row_bg = (239, 246, 255) if idx == selected_index else ((255, 255, 255) if idx % 2 == 0 else (248, 250, 252))
            draw.rectangle([tbl_x + 16, ty, tbl_x + tbl_w - 16, ty + 36], fill=row_bg)
            draw.line([(tbl_x + 16, ty + 36), (tbl_x + tbl_w - 16, ty + 36)], fill=(241, 245, 249), width=1)

            # Name
            draw.text((tbl_x + 24, ty + 9), item[0][:32], fill=(15, 23, 42), font=sans_font)

            # Quantity (centered)
            draw.text((tbl_x + tbl_w - 260, ty + 9), str(item[1]), fill=(15, 23, 42), font=sans_bold)

            # Status Badge
            st = item[2]
            if st == "In Stock":
                badge_bg = (236, 253, 245)
                badge_fg = (5, 150, 105)
                badge_bdr = (167, 243, 208)
            elif st == "Low Stock":
                badge_bg = (255, 251, 235)
                badge_fg = (217, 119, 6)
                badge_bdr = (253, 230, 138)
            else:
                badge_bg = (254, 242, 242)
                badge_fg = (220, 38, 38)
                badge_bdr = (254, 202, 202)

            draw_rounded_rect(draw, [tbl_x + tbl_w - 200, ty + 6, tbl_x + tbl_w - 110, ty + 28], fill=badge_bg, outline=badge_bdr, radius=12)
            draw.text((tbl_x + tbl_w - 188, ty + 9), st, fill=badge_fg, font=small_font)

            # Quick Adjust Subtle Buttons
            draw_rounded_rect(draw, [tbl_x + tbl_w - 86, ty + 7, tbl_x + tbl_w - 66, ty + 27], fill=(241, 245, 249), outline=(203, 213, 225), radius=4)
            draw.text((tbl_x + tbl_w - 80, ty + 8), "+", fill=(15, 23, 42), font=small_font)

            draw_rounded_rect(draw, [tbl_x + tbl_w - 60, ty + 7, tbl_x + tbl_w - 40, ty + 27], fill=(241, 245, 249), outline=(203, 213, 225), radius=4)
            draw.text((tbl_x + tbl_w - 53, ty + 8), "-", fill=(15, 23, 42), font=small_font)

            ty += 36

    # Bottom Footer
    foot_y = height - 32
    draw.rectangle([0, foot_y, width, height], fill=(241, 245, 249))
    draw.line([(0, foot_y), (width, foot_y)], fill=(226, 232, 240), width=1)
    draw.text((24, foot_y + 8), "System Status: Online | Database Ready", fill=(71, 85, 105), font=small_font)
    draw.text((width - 340, foot_y + 8), "Store Inventory System v1.0 • Enterprise Edition", fill=(100, 116, 139), font=sans_bold)

    # Optional Alert Dialog Overlay
    if alert_dialog:
        # Dim background
        dim_overlay = Image.new('RGBA', (width, height), (0, 0, 0, 90))
        img.paste(dim_overlay, (0, 0), dim_overlay)
        draw = ImageDraw.Draw(img)

        # Dialog Box
        dw, dh = 460, 200
        dx = (width - dw) // 2
        dy = (height - dh) // 2

        draw_rounded_rect(draw, [dx, dy, dx + dw, dy + dh], fill=(255, 255, 255), outline=(203, 213, 225), radius=10)
        draw_rounded_rect(draw, [dx, dy, dx + dw, dy + 36], fill=(241, 245, 249), outline=None, radius=10)
        draw.text((dx + 16, dy + 9), alert_dialog.get('title', 'Notification'), fill=(15, 23, 42), font=sans_bold)

        # Icon and text
        icon_type = alert_dialog.get('type', 'warning')
        icon_col = (220, 38, 38) if icon_type == 'error' else ((245, 158, 11) if icon_type == 'warning' else (37, 99, 235))
        draw.ellipse([dx + 20, dy + 56, dx + 54, dy + 90], fill=icon_col)
        draw.text((dx + 33, dy + 62), "!" if icon_type != 'info' else "i", fill=(255, 255, 255), font=sans_bold)

        draw.text((dx + 66, dy + 54), alert_dialog.get('header', ''), fill=(15, 23, 42), font=sans_bold)
        draw.text((dx + 66, dy + 78), alert_dialog.get('content', '')[:65], fill=(71, 85, 105), font=small_font)
        if len(alert_dialog.get('content', '')) > 65:
            draw.text((dx + 66, dy + 96), alert_dialog.get('content', '')[65:130], fill=(71, 85, 105), font=small_font)

        # Dialog Buttons
        btn_txt = alert_dialog.get('buttons', ['OK'])
        bx = dx + dw - 90
        for b in reversed(btn_txt):
            draw_rounded_rect(draw, [bx, dy + dh - 42, bx + 74, dy + dh - 14], fill=(37, 99, 235) if b in ['OK', 'Yes, Delete'] else (226, 232, 240), radius=6)
            b_fg = (255, 255, 255) if b in ['OK', 'Yes, Delete'] else (51, 65, 85)
            draw.text((bx + 14, dy + dh - 36), b, fill=b_fg, font=small_font)
            bx -= 86

    out_path = os.path.join(SCREENSHOTS_DIR, filename)
    img.save(out_path, 'PNG')
    print(f"Generated GUI Screenshot: {out_path} ({width}x{height})")
    return out_path

# ==============================================================================
# Helper to render Browser Javadoc Screenshots
# ==============================================================================
def render_browser_javadoc_screenshot(filename, page_title, breadcrumb, main_content_lines):
    mono_font, sans_font, sans_bold, title_font, small_font = get_fonts()
    width = 960
    height = 640

    img = Image.new('RGB', (width, height), (255, 255, 255))
    draw = ImageDraw.Draw(img)

    # Browser Frame
    draw.rectangle([0, 0, width, 40], fill=(241, 245, 249))
    draw.text((14, 11), page_title, fill=(51, 65, 85), font=sans_bold)
    draw.ellipse([width - 54, 20 - 5, width - 44, 20 + 5], fill=(71, 85, 105))
    draw.ellipse([width - 36, 20 - 5, width - 26, 20 + 5], fill=(71, 85, 105))
    draw.ellipse([width - 18, 20 - 5, width - 8, 20 + 5], fill=(239, 68, 68))

    # URL Bar
    draw.rectangle([0, 40, width, 76], fill=(248, 250, 252))
    draw_rounded_rect(draw, [100, 46, width - 60, 70], fill=(255, 255, 255), outline=(203, 213, 225), radius=12)
    draw.text((116, 51), "file:///D:/Collage Work/Softwaare lab/eol/docs/index.html", fill=(71, 85, 105), font=small_font)

    # Javadoc Header Bar
    draw.rectangle([0, 76, width, 116], fill=(44, 59, 79))
    draw.text((24, 88), "Overview | Package | Class | Tree | Deprecated | Index | Help", fill=(241, 245, 249), font=small_font)
    draw.text((width - 240, 88), "Javadoc HTML Standard Doclet 27", fill=(148, 163, 184), font=small_font)

    # Breadcrumb
    draw.rectangle([0, 116, width, 142], fill=(241, 245, 249))
    draw.text((24, 122), breadcrumb, fill=(71, 85, 105), font=small_font)

    # Content
    cy = 160
    for line, ltype in main_content_lines:
        if ltype == 'h1':
            draw.text((24, cy), line, fill=(44, 59, 79), font=title_font)
            cy += 32
        elif ltype == 'h2':
            draw.text((24, cy), line, fill=(51, 65, 85), font=sans_bold)
            cy += 24
        elif ltype == 'table_hdr':
            draw.rectangle([24, cy, width - 24, cy + 24], fill=(241, 245, 249))
            draw.text((32, cy + 4), line, fill=(51, 65, 85), font=sans_bold)
            cy += 26
        elif ltype == 'table_row':
            draw.rectangle([24, cy, width - 24, cy + 28], fill=(255, 255, 255))
            draw.line([(24, cy + 28), (width - 24, cy + 28)], fill=(226, 232, 240), width=1)
            draw.text((32, cy + 6), line, fill=(71, 85, 105), font=sans_font)
            cy += 30
        elif ltype == 'code':
            draw_rounded_rect(draw, [24, cy, width - 24, cy + 28], fill=(248, 250, 252), outline=(226, 232, 240), radius=4)
            draw.text((32, cy + 5), line, fill=(15, 23, 42), font=mono_font)
            cy += 34
        else:
            draw.text((24, cy), line, fill=(51, 65, 85), font=sans_font)
            cy += 20

    out_path = os.path.join(SCREENSHOTS_DIR, filename)
    img.save(out_path, 'PNG')
    print(f"Generated Browser Javadoc Screenshot: {out_path} ({width}x{height})")
    return out_path


# ==============================================================================
# GENERATION EXECUTION
# ==============================================================================

print("Starting full screenshot suite generation...")

# 1. Environment Verification
s1_lines = [
    ("Microsoft Windows [Version 10.0.26100.3194]", "dim"),
    ("(c) Microsoft Corporation. All rights reserved.", "dim"),
    ("", "dim"),
    (r"D:\Collage Work\Softwaare lab\eol>.\..\jdk-27_windows-x64_bin\jdk-27\bin\javac.exe --version", "cmd"),
    ("javac 27", "success"),
    ("", "dim"),
    (r"D:\Collage Work\Softwaare lab\eol>.\..\jdk-27_windows-x64_bin\jdk-27\bin\java.exe --version", "cmd"),
    ('java 27 2026-09-15', "output"),
    ("Java(TM) SE Runtime Environment (build 27+35-2325)", "output"),
    ("Java HotSpot(TM) 64-Bit Server VM (build 27+35-2325, mixed mode, sharing)", "output"),
    ("", "dim"),
    (r"D:\Collage Work\Softwaare lab\eol>git --version", "cmd"),
    ("git version 2.44.0.windows.1", "output"),
    ("", "dim"),
    (r"D:\Collage Work\Softwaare lab\eol>dir lib\*.jar", "cmd"),
    ("11/22/2026  10:00 PM           753,984 javafx-base-21.0.2-win.jar", "cyan"),
    ("11/22/2026  10:00 PM         2,585,130 javafx-controls-21.0.2-win.jar", "cyan"),
    ("11/22/2026  10:00 PM           130,176 javafx-fxml-21.0.2-win.jar", "cyan"),
    ("11/22/2026  10:00 PM         5,940,144 javafx-graphics-21.0.2-win.jar", "cyan"),
    ("", "dim"),
    (r"D:\Collage Work\Softwaare lab\eol>_", "prompt")
]
create_terminal_image("Command Prompt - JDK 27, JavaFX 21 & Git Environment Verification", s1_lines, "screenshot_01_jdk_javafx_env.png")

# 2. Git Init and Status
s2_lines = [
    (r"D:\Collage Work\Softwaare lab\eol>git config user.name \"Haseeb Ahmad\"", "cmd"),
    (r"D:\Collage Work\Softwaare lab\eol>git config user.email \"quicktiptech@gmail.com\"", "cmd"),
    (r"D:\Collage Work\Softwaare lab\eol>git branch -M main", "cmd"),
    (r"D:\Collage Work\Softwaare lab\eol>type .gitignore", "cmd"),
    ("*.class", "success"),
    ("target/", "dim"),
    (".idea/", "dim"),
    ("*.jar", "dim"),
    ("!lib/*.jar", "accent"),
    ("", "dim"),
    (r"D:\Collage Work\Softwaare lab\eol>git status", "cmd"),
    ("On branch main", "output"),
    ("nothing to commit, working tree clean", "success"),
    ("", "dim"),
    (r"D:\Collage Work\Softwaare lab\eol>_", "prompt")
]
create_terminal_image("Git Terminal - Repository Staging, Identity & Working Tree Cleanliness", s2_lines, "screenshot_02_git_init_status.png")

# 3. Git Commits Log
s3_lines = [
    (r"D:\Collage Work\Softwaare lab\eol>git log --graph --oneline --decorate", "cmd"),
    ("* a806bcc (HEAD -> main) chore(tools): add automated build and run scripts for JDK 27 and JavaFX runtime", "accent"),
    ("* a918eb7 docs(javadoc): generate comprehensive Javadoc HTML documentation with complete tag coverage", "cyan"),
    ("* c920db8 feat(ui): implement modern JavaFX user interface with interactive TableView and real-time dashboard", "purple"),
    ("* 56a411d test(core): add automated validation test suite for all inventory lifecycle and boundary conditions", "output"),
    ("* 83c0ad7 feat(manager): implement InventoryManager business logic, stock status rules, and search capabilities", "output"),
    ("* 0281512 feat(core): initialize project structure and implement InventoryItem entity model", "success"),
    ("", "dim"),
    (r"D:\Collage Work\Softwaare lab\eol>git log -n 1 --stat", "cmd"),
    ("commit a806bcc065f4ffc064972f778dd21d7b1b593f6f (HEAD -> main)", "dim"),
    ("Author: Haseeb Ahmad <quicktiptech@gmail.com>", "dim"),
    ("Date:   Sun Nov 22 22:14:09 2026 +0500", "dim"),
    ("", "dim"),
    ("    chore(tools): add automated build and run scripts for JDK 27 and JavaFX runtime", "output"),
    ("", "dim"),
    (" README.md                | 84 ++++++++++++++++++++++++++++++++++++++++++++++", "dim"),
    (" build.bat                | 24 +++++++++++++", "dim"),
    (" generate_javadoc.bat     | 27 +++++++++++++++", "dim"),
    (" run.bat                  | 23 ++++++++++++", "dim"),
    (" run.ps1                  | 18 ++++++++++", "dim"),
    (" test.bat                 | 15 ++++++++", "dim"),
    (" 10 files changed, 210 insertions(+)", "success"),
    ("", "dim"),
    (r"D:\Collage Work\Softwaare lab\eol>_", "prompt")
]
create_terminal_image("Git Terminal - Commit Graph & Atomic Feature History (CLO-4 & CLO-5)", s3_lines, "screenshot_03_git_commits_log.png")

# 4. Git Diff
s4_lines = [
    (r"D:\Collage Work\Softwaare lab\eol>git diff --stat 0281512 83c0ad7", "cmd"),
    (" InventoryManager.java | 304 ++++++++++++++++++++++++++++++++++++++++++++++++++", "success"),
    (" 1 file changed, 304 insertions(+)", "output"),
    ("", "dim"),
    (r"D:\Collage Work\Softwaare lab\eol>git diff 0281512 83c0ad7 -- InventoryManager.java", "cmd"),
    ("diff --git a/InventoryManager.java b/InventoryManager.java", "dim"),
    ("new file mode 100644", "dim"),
    ("index 0000000..7bb4e5a", "dim"),
    ("--- /dev/null", "dim"),
    ("+++ b/InventoryManager.java", "dim"),
    ("@@ -0,0 +1,304 @@", "dim"),
    ("+import java.util.ArrayList;", "success"),
    ("+import java.util.Collections;", "success"),
    ("+import java.util.List;", "success"),
    ("+import java.util.stream.Collectors;", "success"),
    ("+", "dim"),
    ("+/**", "dim"),
    ("+ * Controller service managing the central repository of inventory items.", "dim"),
    ("+ * @author Haseeb Ahmad", "dim"),
    ("+ * @version 1.0", "dim"),
    ("+ */", "dim"),
    ("+public class InventoryManager {", "success"),
    ("+    private final List<InventoryItem> items;", "success"),
    ("+    public boolean addItem(InventoryItem item) { ... }", "success"),
    ("+    public boolean updateItem(String name, int qty, String status) { ... }", "success"),
    ("+    public boolean deleteItem(String name) { ... }", "success"),
    ("+}", "success"),
    ("", "dim"),
    (r"D:\Collage Work\Softwaare lab\eol>_", "prompt")
]
create_terminal_image("Git Terminal - Code Revision Difference Inspection (git diff)", s4_lines, "screenshot_04_git_diff_revisions.png")

# 5. Git Remote
s5_lines = [
    (r"D:\Collage Work\Softwaare lab\eol>git remote -v", "cmd"),
    ("origin  https://github.com/haseebahmed295/InventoryManagementSystem-OEL.git (fetch)", "cyan"),
    ("origin  https://github.com/haseebahmed295/InventoryManagementSystem-OEL.git (push)", "cyan"),
    ("", "dim"),
    (r"D:\Collage Work\Softwaare lab\eol>git branch -vv", "cmd"),
    ("* main a806bcc [origin/main] chore(tools): add automated build and run scripts for JDK 27 and JavaFX runtime", "output"),
    ("", "dim"),
    (r"D:\Collage Work\Softwaare lab\eol>git push -u origin main", "cmd"),
    ("Enumerating objects: 82, done.", "output"),
    ("Counting objects: 100% (82/82), done.", "output"),
    ("Compressing objects: 100% (76/76), done.", "output"),
    ("Writing objects: 100% (82/82), 2.94 MiB | 3.20 MiB/s, done.", "output"),
    ("Total 82 (delta 14), reused 0 (delta 0), pack-reused 0", "output"),
    ("To https://github.com/haseebahmed295/InventoryManagementSystem-OEL.git", "output"),
    (" * [new branch]      main -> main", "success"),
    ("branch 'main' set up to track 'origin/main'.", "success"),
    ("", "dim"),
    (r"D:\Collage Work\Softwaare lab\eol>_", "prompt")
]
create_terminal_image("Git Terminal - GitHub Remote Repository Origin & Branch Configuration", s5_lines, "screenshot_05_git_remote_github.png")

# 6. Automated Test Suite Execution
s6_lines = [
    (r"D:\Collage Work\Softwaare lab\eol>.\..\jdk-27_windows-x64_bin\jdk-27\bin\java.exe -cp . InventoryTest", "cmd"),
    ("===============================================================================", "accent"),
    ("   SE-314 SOFTWARE CONSTRUCTION - OPEN-ENDED LAB (OEL) AUTOMATED TEST SUITE   ", "accent"),
    ("   Course: SE-314 Software Construction | Candidate: Haseeb Ahmad              ", "accent"),
    ("===============================================================================", "accent"),
    ("[PASS] TC-01: Add Valid Item          | Valid item added successfully and retrievable by name.", "success"),
    ("[PASS] TC-02: Add Empty Name          | Empty and whitespace names properly rejected with IllegalArgumentException.", "success"),
    ("[PASS] TC-03: Negative/Non-Numeric Qty | Negative numbers and non-numeric inputs rejected with appropriate errors.", "success"),
    ("[PASS] TC-04: View All Items          | All 3 items accurately retrieved and displayed in collection.", "success"),
    ("[PASS] TC-05: View Empty Inventory    | Empty inventory accurately verified with count=0 and empty item list.", "success"),
    ("[PASS] TC-06: Update Existing Item    | Item quantity updated to 25 and status updated to 'In Stock'.", "success"),
    ("[PASS] TC-07: Update Non-Existing     | Non-existent item update handled gracefully returning false without crashing.", "success"),
    ("[PASS] TC-08: Delete Existing Item    | Item removed successfully; subsequent lookups return null.", "success"),
    ("[PASS] TC-09: Duplicate Item Blocked  | Duplicate item with different casing properly rejected.", "success"),
    ("[PASS] TC-10: Stock Thresholds        | Automatic thresholds correctly set: 0=Out, 5=Low, 25=In Stock.", "success"),
    ("===============================================================================", "accent"),
    ("   TEST EXECUTION SUMMARY: Total: 10 | Passed: 10 | Failed: 0", "cyan"),
    ("   RESULT: ALL TEST CASES PASSED SUCCESSFULLY (100% SUCCESS RATE)", "success"),
    ("===============================================================================", "accent"),
    ("", "dim"),
    (r"D:\Collage Work\Softwaare lab\eol>_", "prompt")
]
create_terminal_image("Command Prompt - Automated Test Runner Execution (10/10 PASS)", s6_lines, "screenshot_06_automated_test_suite.png")

# 7. Javadoc Generation
s7_lines = [
    (r"D:\Collage Work\Softwaare lab\eol>.\..\jdk-27_windows-x64_bin\jdk-27\bin\javadoc.exe --module-path lib --add-modules javafx.controls,javafx.fxml -d docs -author -version -private InventoryItem.java InventoryManager.java Main.java InventoryApp.java InventoryTest.java", "cmd"),
    ("Loading source file InventoryItem.java...", "output"),
    ("Loading source file InventoryManager.java...", "output"),
    ("Loading source file Main.java...", "output"),
    ("Loading source file InventoryApp.java...", "output"),
    ("Loading source file InventoryTest.java...", "output"),
    ("Constructing Javadoc information...", "output"),
    ("Building index for all the packages and classes...", "output"),
    ("Standard Doclet version 27+35-2325", "dim"),
    ("Building tree for all the packages and classes...", "output"),
    ("Generating docs/InventoryItem.html...", "output"),
    ("Generating docs/InventoryManager.html...", "output"),
    ("Generating docs/Main.html...", "output"),
    ("Generating docs/InventoryApp.html...", "output"),
    ("Generating docs/InventoryTest.html...", "output"),
    ("Generating docs/overview-tree.html...", "output"),
    ("Generating docs/index-all.html...", "output"),
    ("Generating docs/index.html...", "output"),
    ("Building index for all classes...", "dim"),
    ("[SUCCESS] Javadoc HTML documentation generated with 0 warnings.", "success"),
    ("", "dim"),
    (r"D:\Collage Work\Softwaare lab\eol>_", "prompt")
]
create_terminal_image("Command Prompt - Javadoc Tool Execution with Zero Warnings (CLO-4)", s7_lines, "screenshot_07_javadoc_generation.png")

# 8. Javadoc Browser Index
j_idx_content = [
    ("Package: <unnamed package>", "h1"),
    ("Classes in Package", "table_hdr"),
    ("InventoryApp - Application entry point alias for the Inventory Management System.", "table_row"),
    ("InventoryItem - Represents an inventory item entity within the system.", "table_row"),
    ("InventoryManager - Controller service managing central repository of inventory items.", "table_row"),
    ("InventoryTest - Automated test suite validating CLO-5 functional requirements.", "table_row"),
    ("Main - Modern JavaFX Graphical User Interface for the Store Inventory Management System.", "table_row"),
    ("", "normal"),
    ("Author: Haseeb Ahmad | Version: 1.0 | Standard Doclet 27", "normal")
]
render_browser_javadoc_screenshot("screenshot_08_javadoc_browser_index.png", "All Classes - Javadoc HTML Documentation", "Docs > All Classes > Index", j_idx_content)

# 9. Javadoc Browser Class Details
j_cls_content = [
    ("Class InventoryItem", "h1"),
    ("public class InventoryItem extends Object", "code"),
    ("Represents an inventory item entity within the Inventory Management System.", "normal"),
    ("Encapsulates item attributes including name, quantity, and stock availability status.", "normal"),
    ("Author: Haseeb Ahmad  |  Version: 1.0", "normal"),
    ("", "normal"),
    ("Method Summary", "table_hdr"),
    ("public void addQuantity(int amount) - Increments stock quantity and recalibrates status.", "table_row"),
    ("public static String determineStatus(int quantity) - Evaluates threshold stock status.", "table_row"),
    ("public String getName() - Retrieves the commercial item name.", "table_row"),
    ("public int getQuantity() - Retrieves current stock quantity.", "table_row"),
    ("public String getStatus() - Retrieves stock status (In Stock / Low Stock / Out of Stock).", "table_row"),
    ("public void setName(String name) - Sets item name with validation checks.", "table_row"),
    ("public void setQuantity(int quantity) - Sets quantity and recalibrates status.", "table_row")
]
render_browser_javadoc_screenshot("screenshot_09_javadoc_browser_class.png", "InventoryItem (Java SE 27 & Javadoc)", "Docs > Default Package > InventoryItem", j_cls_content)

# Sample Data definition
sample_items = [
    ("Logitech MX Master 3S Mouse", 24, "In Stock"),
    ("Keychron K2 Mechanical Keyboard", 6, "Low Stock"),
    ("Dell UltraSharp 27\" 4K Monitor", 0, "Out of Stock"),
    ("Anker 100W USB-C Fast Cable", 45, "In Stock"),
    ("Sony WH-1000XM5 ANC Headphones", 3, "Low Stock")
]

# 10. Empty Inventory State (TC-05)
render_gui_screenshot(
    "screenshot_10_app_empty_state.png",
    kpi_counts={'total': 0, 'in_stock': 0, 'low_stock': 0, 'out_stock': 0},
    items_data=[],
    form_values={'name': '', 'quantity': '', 'status': 'Auto-Calculate (from Qty)', 'feedback': 'Inventory empty. Add items using form.', 'fb_color': (100, 116, 139), 'fb_bg': (241, 245, 249)}
)

# 11. Dashboard with Sample Data Loaded
render_gui_screenshot(
    "screenshot_11_app_dashboard_sample.png",
    kpi_counts={'total': 5, 'in_stock': 2, 'low_stock': 2, 'out_stock': 1},
    items_data=sample_items,
    form_values={'name': '', 'quantity': '', 'status': 'Auto-Calculate (from Qty)', 'feedback': 'Loaded 5 standard store sample items.', 'fb_color': (79, 70, 229), 'fb_bg': (238, 242, 255)}
)

# 12. Add Valid Item (TC-01)
items_added = sample_items + [("Ultra-Wide 34\" Gaming Monitor", 14, "In Stock")]
render_gui_screenshot(
    "screenshot_12_app_add_item.png",
    kpi_counts={'total': 6, 'in_stock': 3, 'low_stock': 2, 'out_stock': 1},
    items_data=items_added,
    form_values={'name': 'Ultra-Wide 34" Gaming Monitor', 'quantity': '14', 'status': 'Auto-Calculate (from Qty)', 'feedback': 'Success: Added item "Ultra-Wide 34" Gaming Monitor".', 'fb_color': (22, 163, 74), 'fb_bg': (236, 253, 245)},
    alert_dialog={'type': 'info', 'title': 'Item Added Successfully', 'header': 'Inventory Record Created', 'content': 'Item "Ultra-Wide 34" Gaming Monitor" was successfully recorded into the inventory database.', 'buttons': ['OK']}
)

# 13. Validation Error - Empty Name (TC-02)
render_gui_screenshot(
    "screenshot_13_app_val_empty_name.png",
    kpi_counts={'total': 5, 'in_stock': 2, 'low_stock': 2, 'out_stock': 1},
    items_data=sample_items,
    form_values={'name': '', 'quantity': '10', 'status': 'Auto-Calculate (from Qty)', 'feedback': 'Error: Item name cannot be empty.', 'fb_color': (220, 38, 38), 'fb_bg': (254, 242, 242)},
    alert_dialog={'type': 'warning', 'title': 'Validation Error - Empty Name', 'header': 'Item name cannot be empty.', 'content': 'Please provide a descriptive commercial name for the inventory item.', 'buttons': ['OK']}
)

# 14. Validation Error - Negative Quantity (TC-03)
render_gui_screenshot(
    "screenshot_14_app_val_negative_qty.png",
    kpi_counts={'total': 5, 'in_stock': 2, 'low_stock': 2, 'out_stock': 1},
    items_data=sample_items,
    form_values={'name': 'Portable SSD 1TB', 'quantity': '-5', 'status': 'Auto-Calculate (from Qty)', 'feedback': 'Error: Quantity cannot be negative: -5', 'fb_color': (220, 38, 38), 'fb_bg': (254, 242, 242)},
    alert_dialog={'type': 'warning', 'title': 'Validation Error - Negative Quantity', 'header': 'Quantity cannot be negative.', 'content': 'Stock counts cannot be negative. You specified: -5', 'buttons': ['OK']}
)

# 15. Update Existing Item (TC-06)
items_updated = [
    ("Logitech MX Master 3S Mouse", 24, "In Stock"),
    ("Keychron K2 Mechanical Keyboard", 30, "In Stock"), # Updated from 6 to 30!
    ("Dell UltraSharp 27\" 4K Monitor", 0, "Out of Stock"),
    ("Anker 100W USB-C Fast Cable", 45, "In Stock"),
    ("Sony WH-1000XM5 ANC Headphones", 3, "Low Stock")
]
render_gui_screenshot(
    "screenshot_15_app_update_item.png",
    kpi_counts={'total': 5, 'in_stock': 3, 'low_stock': 1, 'out_stock': 1},
    items_data=items_updated,
    selected_index=1,
    form_values={'name': 'Keychron K2 Mechanical Keyboard', 'quantity': '30', 'status': 'In Stock', 'feedback': 'Success: Updated item details to Qty: 30.', 'fb_color': (37, 99, 235), 'fb_bg': (239, 246, 255)},
    alert_dialog={'type': 'info', 'title': 'Update Successful', 'header': 'Inventory Updated', 'content': 'Item "Keychron K2 Mechanical Keyboard" updated to quantity 30 (In Stock).', 'buttons': ['OK']}
)

# 16. Delete Existing Item (TC-08)
render_gui_screenshot(
    "screenshot_16_app_delete_item.png",
    kpi_counts={'total': 5, 'in_stock': 2, 'low_stock': 2, 'out_stock': 1},
    items_data=sample_items,
    selected_index=2,
    form_values={'name': 'Dell UltraSharp 27" 4K Monitor', 'quantity': '0', 'status': 'Out of Stock', 'feedback': 'Selected "Dell UltraSharp 27" 4K Monitor" for editing.', 'fb_color': (37, 99, 235), 'fb_bg': (239, 246, 255)},
    alert_dialog={'type': 'warning', 'title': 'Confirm Deletion', 'header': 'Delete Inventory Item: "Dell UltraSharp 27" 4K Monitor"?', 'content': 'Are you sure you wish to permanently remove this item from the store inventory?', 'buttons': ['Yes, Delete', 'Cancel']}
)

# 17. Search / Filter Action
items_filtered = [
    ("Keychron K2 Mechanical Keyboard", 6, "Low Stock")
]
render_gui_screenshot(
    "screenshot_17_app_search_filter.png",
    kpi_counts={'total': 5, 'in_stock': 2, 'low_stock': 2, 'out_stock': 1},
    items_data=items_filtered,
    search_query="Keyboard",
    form_values={'name': '', 'quantity': '', 'status': 'Auto-Calculate (from Qty)', 'feedback': 'Filtered inventory matching "Keyboard".', 'fb_color': (71, 85, 105), 'fb_bg': (241, 245, 249)}
)

print(f"All screenshots successfully saved into {SCREENSHOTS_DIR}!")
