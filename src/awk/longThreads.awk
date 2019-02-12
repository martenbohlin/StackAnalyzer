#!/usr/bin/awk -f

BEGIN {
    PRINT=0
    BUFF=""
    LINES=0
}
/"[^"]*".*prio=[^ ]* tid=[^ ]* .*/ {
    if (LINES>30) {
	print BUFF
	PRINT=0
    }
    LINES=0
    BUFF=""
}
{
    if (LINES<2000) {
	BUFF=BUFF "\n" $0
	LINES=LINES+1
    }
}

END {
    if (PRINT) {
        print BUFF
    }
}
