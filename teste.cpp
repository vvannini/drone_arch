#include <stdlib.h> 
#include <stdio.h> 
#include <linux/limits.h>
int main() 
{ 
        char resolved_path[PATH_MAX]; 
        realpath("../../", resolved_path); 
        printf("\n%s\n",resolved_path); 
        return 0; 
} 