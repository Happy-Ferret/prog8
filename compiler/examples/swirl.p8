%option enable_floats

~ main {

    const word width = 320
    const word height = 200

    sub start()  {

        _vm_gfx_clearscr(0)

        float t
        byte color

        while(1) {
            float x = sin(t*1.01) + cos(t*1.1234)
            float y = cos(t) + sin(t*0.03456)
            _vm_gfx_pixel(screenx(x), screeny(y), color//16)
            t  += 0.01
            color++
        }
    }

    sub screenx(x: float) -> word {
        return floor(x * flt(width)/4.1) + width // 2
    }
    sub screeny(y: float) -> word {
        return floor(y * flt(height)/4.1) + height // 2
    }
}