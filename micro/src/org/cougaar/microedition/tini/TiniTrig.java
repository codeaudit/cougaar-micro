
package org.cougaar.microedition.tini;

import java.text.*;
import java.lang.*;


public final class TiniTrig {

/*
  //test code
  public static void main(String[] args)
  {
    while (true)
    {
      byte[] inbytes = new byte[128];
      System.out.println("Enter degrees: ");

      try
      {
	System.in.read(inbytes);
      }
      catch (Exception ex)
      {
	System.out.println("Unable to read in bytes");
      }

      String instring = new String(inbytes);
      Double temp = new Double(instring);
      double testvalue = temp.doubleValue();

      System.out.println("You entered " +testvalue);

      if(testvalue == 999.0)
	break;

      double answer = TiniCos(testvalue*Math.PI/180.0);
      System.out.println("The cosine is " +answer);
      answer = TiniSin(testvalue*Math.PI/180.0);
      System.out.println("The sine is " +answer);

    }
  }
*/
  public static double tinisin(double radians)
  {
    //make the value range from 0 to 2pi
    if(radians < 0.0) radians += (2.0*Math.PI);
    if(radians >= (2.0*Math.PI)) radians -= (2.0*Math.PI);

    int octant = (int)(radians/(Math.PI/4.0));
    octant += 1;

    double argument, quadrant, lookupanswer;

    if(octant%2 == 0) //if even
    {
      quadrant = octant*(Math.PI/4.0);
      argument = quadrant - radians;
    }
    else
    {
      quadrant = (octant - 1)*(Math.PI/4.0);
      argument = radians - quadrant;
    }


    double signval = 1.0;
    if(octant == 5 || octant == 6 || octant == 7 || octant == 8)
      signval = -1.0;

    switch (octant)
    {
      case 1:
      case 4:
      case 5:
      case 8:
	   lookupanswer = sinlookup(argument);
	   break;
      default:
	   lookupanswer = coslookup(argument);
	   break;
    }

    if(lookupanswer != 0.0) lookupanswer *= signval;

    return(lookupanswer);
  }

  public static double tinicos(double radians)
  {
    //make the value range from 0 to 2pi
    if(radians < 0.0) radians += (2.0*Math.PI);
    if(radians >= (2.0*Math.PI)) radians -= (2.0*Math.PI);

    int octant = (int)(radians/(Math.PI/4.0));
    octant += 1;

    double argument, quadrant, lookupanswer;

    if(octant%2 == 0) //if even
    {
      quadrant = octant*(Math.PI/4.0);
      argument = quadrant - radians;
    }
    else
    {
      quadrant = (octant - 1)*(Math.PI/4.0);
      argument = radians - quadrant;
    }

    double signval = 1.0;
    if(octant == 3 || octant == 4 || octant == 5 || octant == 6)
      signval = -1.0;

    switch (octant)
    {
      case 2:
      case 3:
      case 6:
      case 7:
	   lookupanswer = sinlookup(argument);
	   break;
      default:
	   lookupanswer = coslookup(argument);
	   break;
    }

    if(lookupanswer != 0.0) lookupanswer *= signval;

    return(lookupanswer);
  }

  public static double coslookup(double x)
  {
    if(x < 0.0 || x > Math.PI/4.0)
    {
	 System.out.println("Error: x out of range");
    }
    int index = (int)Math.round((x/resolution));
    return cosinetable[index];
  }

  private static double sinlookup(double x)
  {
    if(x < 0.0 || x > Math.PI/4.0)
    {
	 System.out.println("Error: x out of range");
    }
    int index = (int)Math.round((x/resolution));
    return sinetable[index];
  }

  static final long numintervals = 90;
  static final double resolution = (Math.PI/4.0)/numintervals;
  static final double cosinetable [] = {1.0, 0.9999619230641713, 0.9998476951563913, 0.9996573249755573,
    0.9993908270190958, 0.9990482215818578, 0.9986295347545738, 0.9981347984218669, 0.9975640502598242,
    0.996917333733128, 0.9961946980917455, 0.9953961983671789, 0.9945218953682733, 0.9935718556765875,
    0.992546151641322, 0.9914448613738104, 0.9902680687415704, 0.9890158633619168, 0.9876883405951378,
    0.9862856015372314, 0.984807753012208, 0.9832549075639546, 0.981627183447664, 0.9799247046208296,
    0.9781476007338057, 0.9762960071199334, 0.9743700647852352, 0.9723699203976766, 0.9702957262759965,
    0.9681476403781077, 0.9659258262890683, 0.963630453208623, 0.9612616959383189,0.958819734868193,
    0.9563047559630354, 0.9537169507482269, 0.9510565162951535, 0.9483236552061993, 0.9455185755993168,
    0.9426414910921784, 0.9396926207859084, 0.9366721892483976, 0.9335804264972017, 0.9304175679820246,
    0.9271838545667874, 0.9238795325112867, 0.9205048534524404, 0.917060074385124, 0.9135454576426009,
    0.9099612708765432, 0.9063077870366499, 0.9025852843498606, 0.898794046299167, 0.8949343616020251,
    0.8910065241883679, 0.8870108331782217, 0.882947592858927, 0.8788171126619654, 0.8746197071393957,
    0.8703556959398997, 0.8660254037844387, 0.8616291604415258, 0.8571673007021123, 0.8526401643540922,
    0.848048096156426, 0.8433914458128857, 0.838670567945424, 0.8338858220671682, 0.8290375725550416,
    0.8241261886220157, 0.8191520442889918, 0.8141155183563192, 0.8090169943749475, 0.8038568606172173,
    0.7986355100472928, 0.7933533402912352, 0.7880107536067219, 0.7826081568524139, 0.7771459614569709,
    0.77162458338772, 0.766044443118978, 0.7604059656000309, 0.754709580222772, 0.7489557207890022,
    0.7431448254773942, 0.737277336810124, 0.7313537016191705, 0.7253743710122876, 0.7193398003386512,
    0.7132504491541816, 0.7071067811865476};

  static final double sinetable [] = {0.0, 0.008726535498373935, 0.01745240643728351, 0.026176948307873153,
    0.03489949670250097, 0.043619387365336, 0.052335956242943835, 0.06104853953485687, 0.0697564737441253,
    0.07845909572784494, 0.08715574274765817, 0.09584575252022398, 0.10452846326765347, 0.11320321376790672,
    0.12186934340514748, 0.13052619222005157,0.13917310096006544, 0.14780941112961063, 0.15643446504023087,
    0.16504760586067765, 0.17364817766693033, 0.18223552549214747, 0.1908089953765448, 0.1993679344171972,
    0.20791169081775934, 0.21643961393810288, 0.224951054343865, 0.2334453638559054, 0.24192189559966773,
    0.25038000405444144, 0.25881904510252074, 0.26723837607825685, 0.27563735581699916, 0.28401534470392265,
    0.29237170472273677, 0.3007057995042731, 0.3090169943749474, 0.31730465640509214, 0.3255681544571567,
    0.3338068592337709, 0.3420201433256687, 0.35020738125946743, 0.35836794954530027, 0.3665012267242973,
    0.374606593415912, 0.3826834323650898, 0.39073112848927377, 0.3987490689252462, 0.4067366430758002,
    0.414693242656239, 0.42261826174069944, 0.43051109680829514, 0.4383711467890774, 0.44619781310980877,
    0.45399049973954675, 0.4617486132350339, 0.4694715627858908, 0.4771587602596084, 0.48480962024633706,
    0.49242356010346716, 0.49999999999999994, 0.5075383629607041, 0.5150380749100542, 0.5224985647159488,
    0.5299192642332049, 0.5372996083468239, 0.5446390350150271, 0.5519369853120581, 0.5591929034707469,
    0.5664062369248328, 0.573576436351046, 0.5807029557109398, 0.5877852522924731, 0.5948227867513413,
    0.6018150231520483, 0.6087614290087207, 0.6156614753256583, 0.6225146366376195, 0.6293203910498374,
    0.636078220277764, 0.6427876096865393, 0.6494480483301837, 0.6560590289905073, 0.6626200482157375,
    0.6691306063588582, 0.6755902076156602, 0.6819983600624985, 0.6883545756937539, 0.6946583704589973,
    0.7009092642998509, 0.7071067811865475};
}

