package pe.edu.upeu.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upeu.presentation.theme.PrimaryCyan
import pe.edu.upeu.presentation.theme.TextDark

@Composable
fun PosDropDownMenu(
    selectedOption: String,
    options: List<String>,
    onOptionSelected:(String) -> Unit,
    modifier: Modifier = Modifier
){
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier){
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable{expanded = true}
                .padding(vertical = 8.dp, horizontal = 4.dp)
        ){
            Text(
                text =  selectedOption,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryCyan
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Opciones desplegables",
                tint = PrimaryCyan
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {expanded = false}
        ){
            options.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = option,
                            color = TextDark

                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}