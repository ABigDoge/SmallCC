int func(int arg)
{
    return arg;
}

int main()
{
    int a, b, c;
	a = 0;
	b = 1;
	c = 2;
	c = a - b*c;
    c = ~a;
    c = func(a) + 1;
    if(a > b){
        a = 1;
    }
    else{
        b = 1;
    }
    for (a = 0; a < 10; a++)
    {
        b += 2;
        if(b > 5)
            break;
    }
    Mars_PrintStr("final b:\n");
    Mars_PrintInt(b);
    return 0;
}
