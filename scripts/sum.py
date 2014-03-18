f = open('personInfo.dat')
t = f.readline()
femaleSum=0
maleSum=0
while t:
    a = t.split('|')
    if a[3]=='Female':
        femaleSum+=int(a[4])
    else:
        maleSum+=int(a[4])
    t = f.readline()

print "male sum", maleSum
print "female sum", femaleSum
