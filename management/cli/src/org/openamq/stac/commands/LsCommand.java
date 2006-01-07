package org.openamq.stac.commands;

import org.openamq.AMQException;
import org.openamq.stac.jmx.CurrentMBean;
import org.openamq.stac.jmx.MBeanServerConnectionContext;
import org.openamq.stac.jmx.MBeanUtils;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import java.util.SortedSet;

/**
 * @author Robert Greig (robert.j.greig@jpmorgan.com)
 */
public class LsCommand
{
    public static void execute(MBeanServerConnectionContext context)
            throws AMQException
    {
        CurrentMBean currentMBean = context.getCurrentMBean();

        SortedSet<MBeanAttributeInfo> directories = currentMBean.getOrderedObjects();
        System.out.println();
        for (MBeanAttributeInfo ai : directories)
        {
            if (!MBeanUtils.isHidden(ai))
            {
                outputAccess(ai);
                System.out.println(" " + ai.getName());
            }
        }
        System.out.println();

        SortedSet<MBeanAttributeInfo> attributes = currentMBean.getOrderedAttributes();
        for (MBeanAttributeInfo ai : attributes)
        {
            outputAccess(ai);
            System.out.printf(" %1$-15s%2$-15s %3$s\n", ai.getName(), "[" + convertType(ai.getType()) + "]",
                              currentMBean.getAttributeValue(ai.getName(), ai.getType()));
        }
        System.out.println();
        SortedSet<MBeanOperationInfo> operations = currentMBean.getOrderedOperations();

        for (MBeanOperationInfo oi : operations)
        {
            System.out.printf("-r-x %1$-15s", oi.getName());
            MBeanParameterInfo[] paramInfos = oi.getSignature();
            System.out.print("[");
            if (paramInfos.length == 0)
            {
                System.out.print("No arguments");
            }

            for (int i = 0; i < paramInfos.length; i++)
            {
                MBeanParameterInfo pi = paramInfos[i];
                System.out.printf("%1$s:%2$s%3$s", pi.getName(), convertType(pi.getType()),
                                  (i < paramInfos.length)?",":"");
            }
            System.out.println("]");
        }
        System.out.println();
    }

    private static void outputAccess(MBeanAttributeInfo ai)
    {
        boolean isObject = ai.getType().equals("javax.management.ObjectName");
        System.out.print(isObject?"d":"-");
        System.out.print(ai.isReadable()?"r":"-");
        System.out.print(ai.isWritable()?"w":"-");
        System.out.print("-");
    }

    /**
     * Converts the type name to a non-Java type (e.g. java.lang.String -> String)
     * @param javaType
     * @return a generic type
     */
    private static String convertType(String javaType)
    {
        if ("java.lang.String".equals(javaType))
        {
            return "String";
        }
        else if ("java.lang.Integer".equals(javaType))
        {
            return "Integer";
        }
        else if ("java.lang.Boolean".equals(javaType))
        {
            return "Boolean";
        }
        else if ("java.lang.Double".equals(javaType))
        {
            return "Double";
        }
        else if ("java.util.Date".equals(javaType))
        {
            return "Date";
        }
        else
        {
            return javaType;
        }
    }


}