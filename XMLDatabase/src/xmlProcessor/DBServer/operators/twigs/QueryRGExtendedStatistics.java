
package xmlProcessor.DBServer.operators.twigs;

/**
 * @author Kamyar
 *
 */
public interface QueryRGExtendedStatistics 
{
	public long getNumberOfExecutionPlans();
	
	public long getNumberOfGroupedExecutionPlans();

	public String getExecutionMode();

}
