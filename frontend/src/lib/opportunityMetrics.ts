/** 机会散点轴口径辅助（PRD 竞争阻力为负值）2026-06-05 */

export function resistanceMagnitude(value: number) {
  return Math.abs(value)
}

export function isLowerResistance(left: number, right: number) {
  return left > right
}
