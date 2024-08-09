package com.example.jettip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.isTraceInProgress
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettip.components.InputField
import com.example.jettip.ui.theme.JetTipTheme
import com.example.jettip.util.calculateTotalPerPerson
import com.example.jettip.util.calculateTotalTip
import com.example.jettip.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JetTipTheme {
                MyApp {
                    //  TopHeader()
                    MainContent()


                }

            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.background) {

        Column(modifier = Modifier.padding(12.dp)) {
            MainContent()


        }


    }

}

//@Preview
@Composable
fun TopHeader(totalPerPerson: Double ) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .height(150.dp)
            .padding(8.dp)
            .clip(shape = CircleShape.copy(all = CornerSize(12.dp))), color = Color(0xFFE9D7F7)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val total = "%.2f".format(totalPerPerson)
            Text(
                text = "Total Per Person",
                //h5
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = "$$total",
                //H4
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Preview
@Composable
fun MainContent() {
    Column(modifier = Modifier.padding(all = 12.dp)) {
        BillFrom() {}
    }


}


@Preview
@Composable
fun BillFrom(
    modifier: Modifier = Modifier,
    onValChange: (String) -> Unit = {},
) {
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current


    val sliderPositionStat = remember {
        mutableStateOf(0f)

    }
    val tipPercentage = (sliderPositionStat.value * 100).toInt()

    val splitByState = remember {
        mutableStateOf(1)
    }
    val range = IntRange(start = 1, endInclusive = 100)
    val tipAmountStat = remember {
        mutableStateOf(0.0)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
    TopHeader(totalPerPerson = totalPerPersonState.value)
    Surface(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)

    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(valueState = totalBillState, labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true, onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())
                    keyboardController?.hide()

                })

            //  if (validState) {
            Row(
                modifier = Modifier.padding(3.dp),
                horizontalArrangement = Arrangement.Start
            )
            {
                Text(
                    text = "Split",
                    modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                    )
                )
                Spacer(modifier = Modifier.width(120.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 3.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    RoundIconButton(
                        imageVector = Icons.Default.Remove,
                        onClick = {
                            splitByState.value =
                                if (splitByState.value > 1) {
                                    splitByState.value - 1
                                } else 1
                            totalPerPersonState.value =
                                calculateTotalPerPerson(totalBill =
                                totalBillState.value.toDouble(),
                                    splitBy = splitByState.value
                                    ,tipPercentage = tipPercentage)


                        })
                    Text(
                        text = "${splitByState.value}",
                        modifier
                            .align(Alignment.CenterVertically)
                            .padding(
                                start = 9.dp, end = 9.dp
                            )
                    )


                    RoundIconButton(
                        imageVector = Icons.Default.Add,
                        onClick = {
                            if (splitByState.value < range.last) {
                                splitByState.value += 1
                                totalPerPersonState.value =
                                    calculateTotalPerPerson(totalBill =
                                    totalBillState.value.toDouble(),
                                        splitBy = splitByState.value
                                        ,tipPercentage = tipPercentage)
                            }
                        })


                }
            }
            //Tip Row
            Row(
                modifier = Modifier
                    .padding(
                        horizontal = 3.dp,
                        vertical = 12.dp
                    )
            ) {
                Text(
                    text = "Tip",
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(200.dp))
                Text(text = "$ ${tipAmountStat.value}",
                    modifier = Modifier.align(Alignment.CenterVertically))


            }
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "$tipPercentage%")
                Spacer(modifier = Modifier.height(14.dp))

                //Slider
                Slider(
                    value = sliderPositionStat.value,
                    onValueChange = { newVal ->
                        sliderPositionStat.value = newVal
                        tipAmountStat.value =
                            calculateTotalTip(
                               totalBill =  totalBillState.value.toDouble(),
                                tipPercentage=tipPercentage
                            )
                        totalPerPersonState.value =
                            calculateTotalPerPerson(totalBill =
                            totalBillState.value.toDouble(),
                                splitBy = splitByState.value
                            ,tipPercentage = tipPercentage)


                    },
                    modifier = Modifier.padding(
                        start = 16.dp, end = 16.dp
                    ),
                    steps = 5,
                    onValueChangeFinished = {

                    }


                )


            }
//            } else {
//                Box() {}
//            }
        }


    }

}




