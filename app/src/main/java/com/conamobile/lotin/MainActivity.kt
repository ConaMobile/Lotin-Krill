package com.conamobile.lotin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.gms.ads.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import android.R.attr.label
import android.annotation.SuppressLint

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import com.google.android.gms.ads.AdView

class MainActivity : AppCompatActivity() {

    private var myClipboard: ClipboardManager? = null
    private lateinit var mAdView: AdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomsheetFragment = bottom_sheet()
        myClipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
    
        //google ads
        MobileAds.initialize(this) {}
        mAdView = adView

        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {}

            override fun onAdFailedToLoad(adError: LoadAdError) {}

            override fun onAdOpened() {}

            override fun onAdClicked() {}

            override fun onAdClosed() {}
        }
        //google ads end

        edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                text_view.text = twocount(edit_text.text.toString())

                val value = s.toString()
                if (value == "") {
                    text_view.text = ""
                }
            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                text_view.text = lotionToKiril(edit_text.text.toString())

                
                val value = s.toString()
                if (value == "") {
                    text_view.text = ""
                }
            }
        })

        text_view.setOnLongClickListener {
            if (text_view.text.isNotEmpty()){
                val clipboard: ClipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(label.toString(), text_view.text)
                clipboard.setPrimaryClip(clip)
                Snackbar.make(it, "Copied", 650).show()
            }
            return@setOnLongClickListener true
        }

        crd1.setOnClickListener {
            val abc = myClipboard?.primaryClip
            if (abc != null) {
                val item = abc.getItemAt(0)
                when {
                    item?.text != "" && item.text != "null" -> {
                        edit_text.setText(item?.text.toString())
                        Snackbar.make(it, "Pasted", 650).show()
                    }
                    else -> {
                        Snackbar.make(it, "Empty", 650).show()
                    }
                }
            } else {
                Snackbar.make(it, "Empty", 650).show()
            }
        }

        crd2.setOnClickListener {
            if (text_view.text?.length!! >= 1) {
                val clipboard: ClipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(label.toString(), text_view.text)
                clipboard.setPrimaryClip(clip)
                Snackbar.make(it, "Copied", 650).show()
            } else {
                Snackbar.make(it, "Empty", 650).show()
            }
        }

        crd3.setOnClickListener {
            Snackbar.make(it, "Cleared", 650).show()
            if (edit_text.text?.length!! >= 1) {
                edit_text.text?.clear()
            } else {
                Snackbar.make(it, "Empty", 650).show()
            }
        }

        crd4.setOnClickListener {
            bottomsheetFragment.show(supportFragmentManager, "BottomSheet Dialog")
        }

    }

    fun twocount(lotin2: String): String {
        var kril2 = ""
        for (c2 in lotin2) {
            when {
                c2.toString() == "ya" -> {
                    kril2 = "${kril2}я"
                }
            }
        }
        return kril2
    }

    fun lotionToKiril(lotin: String): String {
        var kril = ""
        for (c in lotin) {
            when {
                //other
                c.toString() == " " -> {
                    kril = "$kril "
                }
                c.toString() == "1" -> {
                    kril = "${kril}1"
                }
                c.toString() == "2" -> {
                    kril = "${kril}2"
                }
                c.toString() == "3" -> {
                    kril = "${kril}3"
                }
                c.toString() == "4" -> {
                    kril = "${kril}4"
                }
                c.toString() == "5" -> {
                    kril = "${kril}5"
                }
                c.toString() == "6" -> {
                    kril = "${kril}6"
                }
                c.toString() == "7" -> {
                    kril = "${kril}7"
                }
                c.toString() == "8" -> {
                    kril = "${kril}8"
                }
                c.toString() == "9" -> {
                    kril = "${kril}9"
                }
                c.toString() == "0" -> {
                    kril = "${kril}0"
                }
                c.toString() == "'" -> {
                    kril = "${kril}ъ"
                }
                c.toString() == "ç" -> {
                    kril = "${kril}ч"
                }
                c.toString() == "Ç" -> {
                    kril = "${kril}Ч"
                }
                c.toString() == "ъ" -> {
                    kril = "${kril}'"
                }
                c.toString() == "Ò" -> {
                    kril = "${kril}Ў"
                }
                c.toString() == "Ó" -> {
                    kril = "${kril}Ў"
                }
                c.toString() == "Ô" -> {
                    kril = "${kril}Ў"
                }
                c.toString() == "Õ" -> {
                    kril = "${kril}Ў"
                }
                c.toString() == "Ö" -> {
                    kril = "${kril}Ў"
                }
                c.toString() == "Ō" -> {
                    kril = "${kril}Ў"
                }
                c.toString() == "Ő" -> {
                    kril = "${kril}Ў"
                }
                c.toString() == "ò" -> {
                    kril = "${kril}ў"
                }
                c.toString() == "ó" -> {
                    kril = "${kril}ў"
                }
                c.toString() == "ô" -> {
                    kril = "${kril}ў"
                }
                c.toString() == "õ" -> {
                    kril = "${kril}ў"
                }
                c.toString() == "ö" -> {
                    kril = "${kril}ў"
                }
                c.toString() == "ō" -> {
                    kril = "${kril}ў"
                }
                c.toString() == "ő" -> {
                    kril = "${kril}ў"
                }

                c.toString() == "w" -> {
                    kril = "${kril}ш"
                }
                c.toString() == "W" -> {
                    kril = "${kril}Ш"
                }
                c.toString() == "c" -> {
                    kril = "${kril}с"
                }
                c.toString() == "C" -> {
                    kril = "${kril}С"
                }
                c.toString() == "Ц" -> {
                    kril = "${kril}S"
                }
                c.toString() == "ц" -> {
                    kril = "${kril}s"
                }
                c.toString() == "Ы" -> {
                    kril = "${kril}I"
                }
                c.toString() == "ы" -> {
                    kril = "${kril}i"
                }
                c.toString() == "Ь" -> {
                    kril = "${kril}I"
                }
                c.toString() == "ь" -> {
                    kril = "${kril}i"
                }
                c.toString() == "Ю" -> {
                    kril = "${kril}Yu"
                }
                c.toString() == "ю" -> {
                    kril = "${kril}yu"
                }
                c.toString() == "Ё" -> {
                    kril = "${kril}Yo"
                }
                c.toString() == "Щ" -> {
                    kril = "${kril}SH"
                }
                c.toString() == "щ" -> {
                    kril = "${kril}sh"
                }
                c.toString() == "Е" -> {
                    kril = "${kril}Ye"
                }
                c.toString() == "е" -> {
                    kril = "${kril}ye"
                }
                c.toString() == "ё" -> {
                    kril = "${kril}yo"
                }
                c.toString() == "Е" -> {
                    kril = "${kril}E"
                }
                c.toString() == "Я" -> {
                    kril = "${kril}Ya"
                }
                c.toString() == "я" -> {
                    kril = "${kril}ya"
                }
                c.toString() == "Ю" -> {
                    kril = "${kril}Yu"
                }
                c.toString() == "," -> {
                    kril = "${kril},"
                }
                c.toString() == "." -> {
                    kril = "${kril}."
                }
                c.toString() == "+" -> {
                    kril = "${kril}+"
                }
                c.toString() == "×" -> {
                    kril = "${kril}×"
                }
                c.toString() == "÷" -> {
                    kril = "${kril}÷"
                }
                c.toString() == "=" -> {
                    kril = "${kril}="
                }
                c.toString() == "/" -> {
                    kril = "${kril}/"
                }
                c.toString() == "_" -> {
                    kril = "${kril}_"
                }
                c.toString() == "€" -> {
                    kril = "${kril}€"
                }
                c.toString() == "£" -> {
                    kril = "${kril}£"
                }
                c.toString() == "¥" -> {
                    kril = "${kril}¥"
                }
                c.toString() == "₩" -> {
                    kril = "${kril}₩"
                }
                c.toString() == "!" -> {
                    kril = "${kril}!"
                }
                c.toString() == "@" -> {
                    kril = "${kril}@"
                }
                c.toString() == "#" -> {
                    kril = "${kril}#"
                }
                c.toString() == "\$" -> {
                    kril = "${kril}\$"
                }
                c.toString() == "%" -> {
                    kril = "${kril}%"
                }
                c.toString() == "^" -> {
                    kril = "${kril}^"
                }
                c.toString() == "&" -> {
                    kril = "${kril}&"
                }
                c.toString() == "*" -> {
                    kril = "${kril}*"
                }
                c.toString() == "(" -> {
                    kril = "${kril}("
                }
                c.toString() == ")" -> {
                    kril = "${kril})"
                }
                c.toString() == "-" -> {
                    kril = "${kril}-"
                }

                c.toString() == "O'" -> {
                    kril = "${kril}Ў"
                }
                c.toString() == "\"" -> {
                    kril = "${kril}\""
                }
                c.toString() == ":" -> {
                    kril = "${kril}:"
                }
                c.toString() == ";" -> {
                    kril = "${kril};"
                }
                c.toString() == "," -> {
                    kril = "${kril},"
                }
                c.toString() == "?" -> {
                    kril = "${kril}?"
                }
                c.toString() == "`" -> {
                    kril = "${kril}`"
                }
                c.toString() == "~" -> {
                    kril = "${kril}~"
                }
                c.toString() == "\\" -> {
                    kril = "${kril}\\"
                }
                c.toString() == "|" -> {
                    kril = "${kril}|"
                }
                c.toString() == "<" -> {
                    kril = "${kril}<"
                }
                c.toString() == ">" -> {
                    kril = "${kril}>"
                }
                c.toString() == "{" -> {
                    kril = "${kril}{"
                }
                c.toString() == "}" -> {
                    kril = "${kril}}"
                }
                c.toString() == "[" -> {
                    kril = "${kril}["
                }
                c.toString() == "]" -> {
                    kril = "${kril}]"
                }
                c.toString() == "°" -> {
                    kril = "${kril}°"
                }
                c.toString() == "•" -> {
                    kril = "${kril}•"
                }
                c.toString() == "○" -> {
                    kril = "${kril}○"
                }
                c.toString() == "●" -> {
                    kril = "${kril}●"
                }
                c.toString() == "□" -> {
                    kril = "${kril}□"
                }
                c.toString() == "■" -> {
                    kril = "${kril}■"
                }
                c.toString() == "♤" -> {
                    kril = "${kril}♤"
                }
                c.toString() == "♡" -> {
                    kril = "${kril}♡"
                }
                c.toString() == "◇" -> {
                    kril = "${kril}◇"
                }
                c.toString() == "♧" -> {
                    kril = "${kril}♧"
                }
                c.toString() == "☆" -> {
                    kril = "${kril}☆"
                }
                c.toString() == "▪️" -> {
                    kril = "${kril}▪️"
                }
                c.toString() == "¤" -> {
                    kril = "${kril}¤"
                }
                c.toString() == "《" -> {
                    kril = "${kril}《"
                }
                c.toString() == "》" -> {
                    kril = "${kril}》"
                }
                c.toString() == "¡" -> {
                    kril = "${kril}¡"
                }
                c.toString() == "¿" -> {
                    kril = "${kril}¿"
                }
                c.toString() == "ė" -> {
                    kril = "${kril}ė"
                }
                c.toString() == "ę" -> {
                    kril = "${kril}ę"
                }
                c.toString() == "ě" -> {
                    kril = "${kril}ě"
                }
                c.toString() == "ə" -> {
                    kril = "${kril}ə"
                }
                c.toString() == "è" -> {
                    kril = "${kril}è"
                }
                c.toString() == "é" -> {
                    kril = "${kril}é"
                }
                c.toString() == "ê" -> {
                    kril = "${kril}ê"
                }
                c.toString() == "ë" -> {
                    kril = "${kril}ë"
                }
                c.toString() == "ē" -> {
                    kril = "${kril}ē"
                }
                c.toString() == "Ė" -> {
                    kril = "${kril}Ė"
                }
                c.toString() == "Ę" -> {
                    kril = "${kril}Ę"
                }
                c.toString() == "Ě" -> {
                    kril = "${kril}Ě"
                }
                c.toString() == "Ĕ" -> {
                    kril = "${kril}Ĕ"
                }
                c.toString() == "Ə" -> {
                    kril = "${kril}Ə"
                }
                c.toString() == "È" -> {
                    kril = "${kril}È"
                }
                c.toString() == "É" -> {
                    kril = "${kril}É"
                }
                c.toString() == "Ê" -> {
                    kril = "${kril}Ê"
                }
                c.toString() == "Ë" -> {
                    kril = "${kril}Ë"
                }
                c.toString() == "Ē" -> {
                    kril = "${kril}Ē"
                }
                c.toString() == "ŕ" -> {
                    kril = "${kril}ŕ"
                }
                c.toString() == "ř" -> {
                    kril = "${kril}ř"
                }
                c.toString() == "Ŕ" -> {
                    kril = "${kril}Ŕ"
                }
                c.toString() == "Ř" -> {
                    kril = "${kril}Ř"
                }
                c.toString() == "þ" -> {
                    kril = "${kril}þ"
                }
                c.toString() == "ť" -> {
                    kril = "${kril}ť"
                }
                c.toString() == "ț" -> {
                    kril = "${kril}ț"
                }
                c.toString() == "ţ" -> {
                    kril = "${kril}ţ"
                }
                c.toString() == "Þ" -> {
                    kril = "${kril}Þ"
                }
                c.toString() == "Ť" -> {
                    kril = "${kril}Ť"
                }
                c.toString() == "Ț" -> {
                    kril = "${kril}Ț"
                }
                c.toString() == "Ţ" -> {
                    kril = "${kril}Ţ"
                }
                c.toString() == "ý" -> {
                    kril = "${kril}ý"
                }
                c.toString() == "Ý" -> {
                    kril = "${kril}Ý"
                }
                c.toString() == "ı" -> {
                    kril = "${kril}ı"
                }
                c.toString() == "į" -> {
                    kril = "${kril}į"
                }
                c.toString() == "ī" -> {
                    kril = "${kril}ī"
                }
                c.toString() == "ï" -> {
                    kril = "${kril}ï"
                }
                c.toString() == "î" -> {
                    kril = "${kril}î"
                }
                c.toString() == "œ" -> {
                    kril = "${kril}œ"
                }
                c.toString() == "ø" -> {
                    kril = "${kril}ø"
                }
                c.toString() == "Ā" -> {
                    kril = "${kril}Ā"
                }
                c.toString() == "ß" -> {
                    kril = "${kril}ß"
                }
                c.toString() == "ğ" -> {
                    kril = "${kril}ğ"
                }
                c.toString() == "Ņ" -> {
                    kril = "${kril}Ņ"
                }


                //end
                //KATTA HARFLAR lotin
                c.toString() == "A" -> {
                    kril = "${kril}A"
                }
                c.toString() == "B" -> {
                    kril = "${kril}Б"
                }
                c.toString() == "D" -> {
                    kril = "${kril}Д"
                }
                c.toString() == "E" -> {
                    kril = "${kril}Э"
                }
                c.toString() == "F" -> {
                    kril = "${kril}Ф"
                }
                c.toString() == "G" -> {
                    kril = "${kril}Г"
                }
                c.toString() == "H" -> {
                    kril = "${kril}Ҳ"
                }
                c.toString() == "I" -> {
                    kril = "${kril}И"
                }
                c.toString() == "J" -> {
                    kril = "${kril}Ж"
                }
                c.toString() == "K" -> {
                    kril = "${kril}К"
                }
                c.toString() == "L" -> {
                    kril = "${kril}Л"
                }
                c.toString() == "M" -> {
                    kril = "${kril}М"
                }
                c.toString() == "N" -> {
                    kril = "${kril}Н"
                }
                c.toString() == "O" -> {
                    kril = "${kril}О"
                }
                c.toString() == "P" -> {
                    kril = "${kril}П"
                }
                c.toString() == "Q" -> {
                    kril = "${kril}Қ"
                }
                c.toString() == "R" -> {
                    kril = "${kril}Р"
                }
                c.toString() == "S" -> {
                    kril = "${kril}С"
                }
                c.toString() == "T" -> {
                    kril = "${kril}Т"
                }
                c.toString() == "U" -> {
                    kril = "${kril}У"
                }
                c.toString() == "V" -> {
                    kril = "${kril}В"
                }
                c.toString() == "X" -> {
                    kril = "${kril}Х"
                }
                c.toString() == "Y" -> {
                    kril = "${kril}Й"
                }
                c.toString() == "Z" -> {
                    kril = "${kril}З"
                }
                c.toString() == "X" -> {
                    kril = "${kril}Х"
                }

                //KATTA HARFLAR END

                //KICHIK HARFLAR lotin
                c.toString() == "a" -> {
                    kril = "${kril}а"
                }
                c.toString() == "b" -> {
                    kril = "${kril}б"
                }
                c.toString() == "d" -> {
                    kril = "${kril}д"
                }
                c.toString() == "e" -> {
                    kril = "${kril}э"
                }
                c.toString() == "f" -> {
                    kril = "${kril}ф"
                }
                c.toString() == "g" -> {
                    kril = "${kril}г"
                }
                c.toString() == "h" -> {
                    kril = "${kril}ҳ"
                }
                c.toString() == "i" -> {
                    kril = "${kril}и"
                }
                c.toString() == "j" -> {
                    kril = "${kril}ж"
                }
                c.toString() == "k" -> {
                    kril = "${kril}к"
                }
                c.toString() == "l" -> {
                    kril = "${kril}л"
                }
                c.toString() == "m" -> {
                    kril = "${kril}м"
                }
                c.toString() == "n" -> {
                    kril = "${kril}н"
                }
                c.toString() == "o" -> {
                    kril = "${kril}о"
                }
                c.toString() == "p" -> {
                    kril = "${kril}п"
                }
                c.toString() == "q" -> {
                    kril = "${kril}қ"
                }
                c.toString() == "r" -> {
                    kril = "${kril}р"
                }
                c.toString() == "s" -> {
                    kril = "${kril}с"
                }
                c.toString() == "t" -> {
                    kril = "${kril}т"
                }
                c.toString() == "u" -> {
                    kril = "${kril}у"
                }
                c.toString() == "v" -> {
                    kril = "${kril}в"
                }
                c.toString() == "x" -> {
                    kril = "${kril}х"
                }
                c.toString() == "y" -> {
                    kril = "${kril}й"
                }
                c.toString() == "z" -> {
                    kril = "${kril}з"
                }
                c.toString() == "x" -> {
                    kril = "${kril}х"
                }
                c.toString() == "sh" -> {
                    kril = "${kril}ш"
                }
                c.toString() == "ch" -> {
                    kril = "${kril}ч"
                }
                //KICHIK HARFLAR END


                //return

                //KATTA HARFLAR krill
                c.toString() == "A" -> {
                    kril = "${kril}A"
                }
                c.toString() == "Б" -> {
                    kril = "${kril}B"
                }
                c.toString() == "Д" -> {
                    kril = "${kril}D"
                }
                c.toString() == "Э" -> {
                    kril = "${kril}E"
                }
                c.toString() == "Ф" -> {
                    kril = "${kril}F"
                }
                c.toString() == "Г" -> {
                    kril = "${kril}G"
                }
                c.toString() == "Ҳ" -> {
                    kril = "${kril}H"
                }
                c.toString() == "И" -> {
                    kril = "${kril}I"
                }
                c.toString() == "Ж" -> {
                    kril = "${kril}J"
                }
                c.toString() == "К" -> {
                    kril = "${kril}K"
                }
                c.toString() == "Л" -> {
                    kril = "${kril}L"
                }
                c.toString() == "М" -> {
                    kril = "${kril}M"
                }
                c.toString() == "Н" -> {
                    kril = "${kril}N"
                }
                c.toString() == "О" -> {
                    kril = "${kril}O"
                }
                c.toString() == "П" -> {
                    kril = "${kril}P"
                }
                c.toString() == "Қ" -> {
                    kril = "${kril}Q"
                }
                c.toString() == "Р" -> {
                    kril = "${kril}R"
                }
                c.toString() == "С" -> {
                    kril = "${kril}S"
                }
                c.toString() == "Т" -> {
                    kril = "${kril}T"
                }
                c.toString() == "У" -> {
                    kril = "${kril}U"
                }
                c.toString() == "В" -> {
                    kril = "${kril}V"
                }
                c.toString() == "Х" -> {
                    kril = "${kril}X"
                }
                c.toString() == "Й" -> {
                    kril = "${kril}Y"
                }
                c.toString() == "З" -> {
                    kril = "${kril}Z"
                }
                c.toString() == "Х" -> {
                    kril = "${kril}X"
                }
                c.toString() == "Ш" -> {
                    kril = "${kril}SH"
                }
                c.toString() == "Ч" -> {
                    kril = "${kril}CH"
                }
                //KATTA HARFLAR END

                //KICHIK HARFLAR krill
                c.toString() == "а" -> {
                    kril = "${kril}a"
                }
                c.toString() == "б" -> {
                    kril = "${kril}b"
                }
                c.toString() == "д" -> {
                    kril = "${kril}d"
                }
                c.toString() == "э" -> {
                    kril = "${kril}e"
                }
                c.toString() == "ф" -> {
                    kril = "${kril}f"
                }
                c.toString() == "г" -> {
                    kril = "${kril}g"
                }
                c.toString() == "ҳ" -> {
                    kril = "${kril}h"
                }
                c.toString() == "и" -> {
                    kril = "${kril}i"
                }
                c.toString() == "ж" -> {
                    kril = "${kril}j"
                }
                c.toString() == "к" -> {
                    kril = "${kril}k"
                }
                c.toString() == "л" -> {
                    kril = "${kril}l"
                }
                c.toString() == "м" -> {
                    kril = "${kril}m"
                }
                c.toString() == "н" -> {
                    kril = "${kril}n"
                }
                c.toString() == "о" -> {
                    kril = "${kril}o"
                }
                c.toString() == "п" -> {
                    kril = "${kril}p"
                }
                c.toString() == "қ" -> {
                    kril = "${kril}q"
                }
                c.toString() == "р" -> {
                    kril = "${kril}r"
                }
                c.toString() == "с" -> {
                    kril = "${kril}s"
                }
                c.toString() == "т" -> {
                    kril = "${kril}t"
                }
                c.toString() == "у" -> {
                    kril = "${kril}u"
                }
                c.toString() == "в" -> {
                    kril = "${kril}v"
                }
                c.toString() == "х" -> {
                    kril = "${kril}x"
                }
                c.toString() == "й" -> {
                    kril = "${kril}y"
                }
                c.toString() == "з" -> {
                    kril = "${kril}z"
                }
                c.toString() == "х" -> {
                    kril = "${kril}x"
                }
                c.toString() == "ш" -> {
                    kril = "${kril}sh"
                }
                c.toString() == "ч" -> {
                    kril = "${kril}ch"
                }
                //KICHIK HARFLAR END
            }

        }
        return kril
    }

}