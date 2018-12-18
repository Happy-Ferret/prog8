%import c64utils
%import mathlib
%option enable_floats


~ main {

    sub start()  {

        uword v1
        uword v2

        c64scr.print_byte(X)
        c64.CHROUT('\n')

        v1 = 100
        v2 = 64444
        if v1==v2
            c64.STROUT("error in 100==64444!\n")
        else
            c64.STROUT("ok: 100 not == 64444\n")

        if v1!=v2
            c64.STROUT("ok: 100 != 64444\n")
        else
            c64.STROUT("error in 100!=64444!\n")

        if v1<v2
            c64.STROUT("ok: 100 < 64444\n")
        else
            c64.STROUT("error in 100<64444!\n")

        if v1<=v2
            c64.STROUT("ok: 100 <= 64444\n")
        else
            c64.STROUT("error in 100<=64444!\n")

        if v1>v2
            c64.STROUT("error in 100>64444!\n")
        else
            c64.STROUT("ok: 100 is not >64444\n")

        if v1>=v2
            c64.STROUT("error in 100>=64444!\n")
        else
            c64.STROUT("ok: 100 is not >=64444\n")


        v1 = 5555
        v2 = 322
        if v1==v2
            c64.STROUT("error in 5555==322!\n")
        else
            c64.STROUT("ok: 5555 not == 322\n")

        if v1!=v2
            c64.STROUT("ok: 5555 != 322\n")
        else
            c64.STROUT("error in 5555!=322!\n")

        if v1<v2
            c64.STROUT("error in 5555<322!\n")
        else
            c64.STROUT("ok: 5555 is not < 322\n")

        if v1<=v2
            c64.STROUT("error in 5555<=322!\n")
        else
            c64.STROUT("ok: 5555 is not <= 322\n")

        if v1>v2
            c64.STROUT("ok: 5555 > 322\n")
        else
            c64.STROUT("error in 5555>322!\n")

        if v1>=v2
            c64.STROUT("ok: 5555 >= 322\n")
        else
            c64.STROUT("error in 5555>=322!\n")

        v1 = 322
        v2 = 322
        if v1==v2
            c64.STROUT("ok: 322 == 322\n")
        else
            c64.STROUT("error in 322==322!\n")

        if v1!=v2
            c64.STROUT("error in 322!=322!\n")
        else
            c64.STROUT("ok: 322 is not != 322\n")

        if v1<v2
            c64.STROUT("error in 322<322!\n")
        else
            c64.STROUT("ok: 322 is not < 322\n")

        if v1<=v2
            c64.STROUT("ok: 322 <= 322\n")
        else
            c64.STROUT("error in 322<=322!\n")

        if v1>v2
            c64.STROUT("error in 322>322!\n")
        else
            c64.STROUT("ok: 322 is not > 322\n")

        if v1>=v2
            c64.STROUT("ok: 322 >= 322\n")
        else
            c64.STROUT("error in 322>=322!\n")

        c64scr.print_byte(X)
        c64.CHROUT('\n')

    }

}