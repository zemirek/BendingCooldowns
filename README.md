# BendingCooldowns
BendingCooldowns is a Spigot plugin designed to manage cooldowns in [ProjectKorra](https://projectkorra.com/). This plugin allows for more complex control over ability cooldowns by defining custom interactions, group-based cooldowns, and specific conditions under which cooldowns are applied.

## Compatibility
- **Spigot**: `1.18`
- **ProjectKorra**: `1.10.0`

## Features
- **Custom Cooldowns**: Define cooldowns between different abilities with precise control.
- **Ability Groups**: Use groups to apply cooldowns to multiple abilities at once.
- **Cooldown Flags**: Control when cooldowns are applied (start, progress, end, cooldown, collision).

## Configuration
- **Groups**: Define sets of abilities that can be referenced collectively.
- **Cooldowns**: Assign cooldowns between abilities with optional flags.
- **Flags**:
    - `s` (Start): Cooldown applies when the ability starts.
    - `p` (Progress): Cooldown applies as the ability progresses.
    - `e` (End): Cooldown applies when the ability ends.
    - `c` (Cooldown): Applies when the ability naturally enters cooldown.
    - `C` (Collision): Applies cooldown when ability collides. You can specify the ability or group for example: `-C(FireBlast)`.

## Commands
- **`/bendingcooldowns reload`** - Reloads the configuration file. Permission: `bendingcooldowns.admin`
