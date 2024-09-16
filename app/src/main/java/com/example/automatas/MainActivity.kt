package com.example.automatas

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Stack

class MainActivity : AppCompatActivity() {

    private lateinit var display: EditText
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        window.navigationBarColor = ContextCompat.getColor(this, R.color.inicial)
        window.statusBarColor = ContextCompat.getColor(this, R.color.inicial)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        display = findViewById(R.id.display)
        resultTextView = findViewById(R.id.resultTextView) // Asociar el nuevo TextView
    }

    fun onDigit(view: android.view.View) {
        val button = view as Button
        display.append(button.text)
    }

    fun onOperator(view: android.view.View) {
        val button = view as Button
        display.append(button.text)
    }

    fun onClear(view: android.view.View) {
        display.text.clear()
    }

    fun onEqual(view: android.view.View) {
        val expression = display.text.toString()
        val postfix = infixToPostfix(expression)
        val prefix = infixToPrefix(expression)
        resultTextView.text =
            "Posfijo: $postfix\nPrefijo: $prefix" // Mostrar el resultado en el TextView
    }

    // Método que borra solo el último carácter del EditText
    fun onBorrar(view: android.view.View) {
        val text = display.text.toString()
        if (text.isNotEmpty()) {
            // Remover el último carácter del texto
            display.setText(text.dropLast(1))
            // Mover el cursor al final del texto
            display.setSelection(display.text.length)
        }
    }

    private fun infixToPostfix(expression: String): String {
        val stack = Stack<Char>()
        val result = StringBuilder()
        val precedence = mapOf('+' to 1, '-' to 1, '*' to 2, '/' to 2, '^' to 3)

        var numberBuffer = StringBuilder()  // Para almacenar números con varios dígitos

        for (char in expression) {
            when {
                char.isDigit() -> {
                    numberBuffer.append(char) // Almacenar dígitos consecutivos en el buffer
                }

                char == '(' -> {
                    if (numberBuffer.isNotEmpty()) {
                        result.append(numberBuffer).append(", ") // Añadir el número almacenado
                        numberBuffer.clear() // Limpiar el buffer
                    }
                    stack.push(char)
                }

                char == ')' -> {
                    if (numberBuffer.isNotEmpty()) {
                        result.append(numberBuffer).append(", ") // Añadir el número almacenado
                        numberBuffer.clear()
                    }
                    while (stack.isNotEmpty() && stack.peek() != '(') {
                        result.append(stack.pop())
                            .append(", ") // Añadir espacio después de cada operador
                    }
                    stack.pop()
                }

                char in precedence.keys -> {
                    if (numberBuffer.isNotEmpty()) {
                        result.append(numberBuffer).append(", ") // Añadir el número almacenado
                        numberBuffer.clear()
                    }
                    while (stack.isNotEmpty() && stack.peek() != '(' &&
                        precedence[char]!! <= precedence[stack.peek()]!!
                    ) {
                        result.append(stack.pop())
                            .append(", ") // Añadir espacio después de cada operador
                    }
                    stack.push(char)
                }
            }
        }

        if (numberBuffer.isNotEmpty()) {
            result.append(numberBuffer).append(", ") // Añadir el número restante si existe
        }

        while (stack.isNotEmpty()) {
            result.append(stack.pop()).append(", ") // Añadir espacio después de cada operador
        }

        return result.toString().trim() // Remover espacio final
    }

    private fun infixToPrefix(expression: String): String {
        val reversedExpression = expression.reversed().map {
            when (it) {
                '(' -> ')'
                ')' -> '('
                else -> it
            }
        }.joinToString("")

        val reversedPostfix = infixToPostfix(reversedExpression)
        return reversedPostfix.reversed().split(", ").reversed()
            .joinToString(", ") // Añadir espacios entre operadores y números
    }
}