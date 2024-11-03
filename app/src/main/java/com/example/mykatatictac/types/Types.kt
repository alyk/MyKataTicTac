package com.example.mykatatictac.types

enum class State { Welcome, Init, Player1, Player2, Result, Error }
enum class UiAction { NewGame, Exit, Restart, Error }
enum class TurnResult { Player1, Player2, Draw, Next, Error }