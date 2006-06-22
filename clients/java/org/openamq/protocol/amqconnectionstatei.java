//  Object used by amqconnectionstate
//  Generated by LIBERO 2.4 on 21 Jun, 2006, 20:24.
//  Schema file used: lrschema.jav.
package org.openamq.protocol;

import java.awt.*;
import java.applet.*;
import java.net.*;
import java.util.*;
import java.io.*;


abstract public class amqconnectionstatei
{
    //- Variables used by dialog interpreter --------------------------------

    private int
        _LR_event,                  //  Event for state transition
        _LR_state,                  //  Current dialog state
        _LR_savest,                 //  Saved dialog state
        _LR_index;                  //  Index of methods function

    public int
        the_next_event,             //  Next event from module
        the_exception_event;        //  Exception event from module

    private boolean
        exception_raised;           //  TRUE if exception raised


    //- Symbolic constants and event numbers --------------------------------

    private static int
        _LR_STOP            = 0xFFFF,
        _LR_NULL_EVENT      = -2;
    public static int
        _LR_STATE_after_init = 0,
        _LR_STATE_connection_opening = 1,
        _LR_STATE_connection_opened = 2,
        _LR_STATE_connection_closed = 3,
        connection_close_event = 0,
        connection_close_ok_event = 1,
        connection_finished_event = 2,
        connection_open_ok_event = 3,
        connection_tune_event = 4,
        ok_event            = 5,
        protocol_initiation_ok_event = 6,
        terminate_event     = -1;

    //- Static areas --------------------------------------------------------

    private static int _LR_nextst [][] = {
        { 0,0,0,0,0,1,0 },
        { 0,0,0,0,2,0,1 },
        { 3,3,0,2,0,0,0 },
        { 0,0,3,0,0,0,0 }
    };

    private static int _LR_action [][] = {
        { 0,0,0,0,0,1,0 },
        { 0,0,0,0,3,0,2 },
        { 5,6,0,4,0,0,0 },
        { 0,0,7,0,0,0,0 }
    };

    private static int _LR_vector [][] = {
        {0},
        {0,1,_LR_STOP},
        {2,1,_LR_STOP},
        {3,4,1,_LR_STOP},
        {1,_LR_STOP},
        {5,_LR_STOP},
        {_LR_STOP},
        {6,_LR_STOP}
    };

    abstract public void initialise_the_program ();
    abstract public void get_external_event ();
    abstract public void send_protocol_initiation ();
    abstract public void expect_frame ();
    abstract public void connection_start_ok ();
    abstract public void connection_tune_ok ();
    abstract public void connection_open ();
    abstract public void connection_close_ok ();
    abstract public void clean_up ();

    //- Dialog interpreter starts here --------------------------------------

    public int execute ()
    {
        int
            feedback = 0,
            index,
            next_module;

        _LR_state = 0;                  //  First state is always zero
        initialise_the_program ();
        while (the_next_event != terminate_event)
          {
            _LR_event = the_next_event;
            if (_LR_event >= 7 || _LR_event < 0)
              {
                String buffer;
                buffer  = "Connection state " + _LR_state + " - event " + _LR_event;
                buffer += " is out of range";
                System.out.println (buffer);
                break;
              }
            _LR_savest = _LR_state;
            _LR_index  = _LR_action [_LR_state][_LR_event];
            if (_LR_index == 0)
              {
                String buffer;
                buffer  = "Connection state " + _LR_state + " - event " + _LR_event;
                buffer += " is not accepted";
                System.out.println (buffer);
                break;
              }
            the_next_event          = _LR_NULL_EVENT;
            the_exception_event     = _LR_NULL_EVENT;
            exception_raised        = false;
            next_module             = 0;

            for (;;)
              {
                index = _LR_vector [_LR_index][next_module];
                if ((index == _LR_STOP)
                || (exception_raised))
                break;
                switch (index)
                  {
                    case 0: send_protocol_initiation (); break;
                    case 1: expect_frame (); break;
                    case 2: connection_start_ok (); break;
                    case 3: connection_tune_ok (); break;
                    case 4: connection_open (); break;
                    case 5: connection_close_ok (); break;
                    case 6: clean_up (); break;
                  }
                  next_module++;
              }
            if (exception_raised)
              {
                if (the_exception_event != _LR_NULL_EVENT)
                    _LR_event = the_exception_event;
                the_next_event = _LR_event;
              }
            else
                _LR_state = _LR_nextst [_LR_state][_LR_event];

            if (the_next_event == _LR_NULL_EVENT)
              {
                get_external_event ();
                if (the_next_event == _LR_NULL_EVENT)
                  {
                    String buffer;
                    buffer  = "No event set after connection event " + _LR_event;
                    buffer += " in state " + _LR_state;
                    System.out.println (buffer);
                    break;
                  }
              }
          }
        return (feedback);
    }

    //- Standard dialog routines --------------------------------------------
    public void raise_exception (int event)
    {
        exception_raised = true;
        if (event >= 0)
            the_exception_event = event;
    }

}
