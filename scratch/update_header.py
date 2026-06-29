import sys
import re

def read_file(path):
    with open(path, 'r', encoding='utf-8') as f:
        return f.read()

def write_file(path, content):
    with open(path, 'w', encoding='utf-8') as f:
        f.write(content)

java_svg_content = read_file('temp_svgs/java.svg')
java_paths = re.search(r'<svg[^>]*>(.*)</svg>', java_svg_content, re.IGNORECASE).group(1)

pg_svg_content = read_file('temp_svgs/postgres.svg')
pg_paths = re.search(r'<svg[^>]*>(.*)</svg>', pg_svg_content, re.IGNORECASE).group(1)

css_svg_content = read_file('temp_svgs/css3.svg')
css_paths = re.search(r'<svg[^>]*>(.*)</svg>', css_svg_content, re.IGNORECASE).group(1)

header = read_file('assets/header-animation.svg')

# Java Pill
header = re.sub(
    r'<text x="42" y="17"[^>]*>☕ Java 21</text>',
    f'<svg x="8" y="5" width="16" height="16" viewBox="0 0 128 128">{java_paths}</svg>\n    <text x="28" y="17" font-family="\'Segoe UI\', Arial, sans-serif" font-size="11" fill="#c084fc" font-weight="600">Java 21</text>',
    header
)

# JavaFX Pill
header = re.sub(
    r'<text x="137" y="17"[^>]*>🎨 JavaFX</text>',
    f'<svg x="103" y="5" width="16" height="16" viewBox="0 0 128 128">{css_paths}</svg>\n    <text x="123" y="17" font-family="\'Segoe UI\', Arial, sans-serif" font-size="11" fill="#22d3ee" font-weight="600">JavaFX</text>',
    header
)

# PostgreSQL Pill
header = re.sub(
    r'<text x="242" y="17"[^>]*>🐘 PostgreSQL</text>',
    f'<svg x="198" y="5" width="16" height="16" viewBox="0 0 128 128">{pg_paths}</svg>\n    <text x="218" y="17" font-family="\'Segoe UI\', Arial, sans-serif" font-size="11" fill="#60a5fa" font-weight="600">PostgreSQL</text>',
    header
)

# Remove emojis from others
header = re.sub(r'⚡ NIO', 'NIO', header)
header = re.sub(r'📋 Log4j2', 'Log4j2', header)
header = re.sub(r'🔗 JDBC', 'JDBC', header)

write_file('assets/header-animation-v2.svg', header)

print("Updated header animation SVG.")
